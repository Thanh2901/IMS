import { Text, Box, Flex, Spinner, Heading, VStack } from "@chakra-ui/react";
import { useRef, useEffect } from "react";
import { Server, Clock, Link as LinkIcon } from "react-feather";
import { useGetCameraStreamingQuery } from "../../redux/service/camera";
import { Camera } from "../../type/models";
import Hls from "hls.js";

const VideoStream: React.FC<{ camera?: Camera }> = ({ camera }) => {
  const { data: videoUrl, isLoading, isError } = useGetCameraStreamingQuery(camera?.id as string, { skip: !camera });
  const videoRef = useRef<HTMLVideoElement>(null);

  useEffect(() => {
    if (videoUrl && videoRef.current) {
      const video = videoRef.current;
      let hls: Hls | null = null;

      if (video.canPlayType("application/vnd.apple.mpegurl")) {
        console.log("Video can play using mpegurl");
        video.src = videoUrl;
        video.load();
      } else if (Hls.isSupported()) {
        console.log("Hls is supported");
        hls = new Hls();
        hls.loadSource(videoUrl + '/index.m3u8');
        hls.attachMedia(video);
      }

      return () => {
        if (hls) {
          hls.destroy();
        }
      };
    }
  }, [videoUrl]);

  if (!camera) {
    return (
      <Flex flex="1" align="center" justify="center" bg="gray.100" overflow="hidden">
        <Text color="gray.500">Select a camera to view stream</Text>
      </Flex>
    );
  }

  if (isLoading) {
    return (
      <Flex flex="1" align="center" justify="center" bg="black">
        <Spinner color="white" />
      </Flex>
    );
  }

  if (isError || !videoUrl) {
    return (
      <Flex flex="1" align="center" justify="center" bg="black">
        <Text color="red.500">Failed to load stream</Text>
      </Flex>
    );
  }

  return (
    <Flex flex="1" direction="column" bg="black" position="relative" overflow="hidden">
      {/* Video Stream */}
      <Box
        as="video"
        ref={videoRef}
        width="100%"
        height="100%"
        controls
        autoPlay
        playsInline
        style={{ objectFit: "contain" }} // Ensure proper resizing
      />

      {/* Camera Details Overlay */}
      {/* <Box position="absolute" bottom="0" left="0" right="0" bg="rgba(0,0,0,0.7)" color="white" p={4}>
        <Heading size="md" mb={2} isTruncated>
          {camera?.name ?? "Unknown Camera"}
        </Heading>
        <VStack align="stretch" spacing={1}>
          <Flex alignItems="center">
            <Server size={16} />
            <Text ml={2} isTruncated>
              IP Address: {camera?.ipAddress ?? "N/A"}
            </Text>
          </Flex>
          <Flex alignItems="center">
            <LinkIcon size={16} />
            <Text ml={2} isTruncated>
              RTSP: {camera?.rtsp ?? "N/A"}
            </Text>
          </Flex>
          <Flex alignItems="center">
            <Clock size={16} />
            <Text ml={2}>Imaging Port: {camera?.imagingPort ?? "N/A"}</Text>
          </Flex>
        </VStack>
      </Box> */}

    </Flex>
  );
};

export default VideoStream;