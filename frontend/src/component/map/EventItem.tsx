import { Box, Text, Flex, useColorModeValue, Badge, Button, HStack, Tooltip } from "@chakra-ui/react";
import { AlertCircle, CheckCircle, Check, Image as ImageIcon, Clock, Video } from "react-feather";
import { EventStatus, InfraStatus } from "../../type/models";
import { Event } from "../../type/models";
import { formatDate, getEventStatusColor, getStatusColor } from "../../type/utils";
import ImageModal from "../common/ImageModal";
import { useDisclosure } from "@chakra-ui/react";
import CheckEventPopup from "./CheckEventPopup";
import { useUpdateRepairEventMutation } from "../../redux/service/infrastructure";
import useCustomToast from "../../hook/useCustomToast";
import { useGetScheduleQuery } from "../../redux/service/camera";

interface EventItemProps {
  item: Event;
  isLast: boolean;
  isFirst: boolean;
}

const EventItem: React.FC<EventItemProps> = ({ item, isLast, isFirst }) => {
  const showToast = useCustomToast();
  const iconColor = useColorModeValue("blue.500", "blue.300");
  const timelineColor = useColorModeValue("gray.200", "gray.600");
  const cardBg = useColorModeValue("white", "gray.800");
  const cardBorder = useColorModeValue("gray.100", "gray.700");
  const textColor = useColorModeValue("gray.800", "gray.100");
  const subTextColor = useColorModeValue("gray.600", "gray.400");
  const { data: schedule } = useGetScheduleQuery(item.scheduleId);
  console.log(schedule);
  const { isOpen: isImageModalOpen, onOpen: onImageModalOpen, onClose: onImageModalClose } = useDisclosure();
  const { isOpen: isCheckEventPopupOpen, onOpen: onCheckEventPopupOpen, onClose: onCheckEventPopupClose } = useDisclosure();
  const [updateRepairEvent, { isLoading }] = useUpdateRepairEventMutation();

  const handleUpdateRepairEvent = async () => {
    try {
      await updateRepairEvent(item.infraObject.id)
        .unwrap()
        .then((response) => {
          console.log(response);
          showToast("Success", "Repair event marked as complete", "success");
        });
    } catch (error: any) {
      showToast("Error", error.message, "error");
    }
  };

  return (
    <Flex>
      {/* Timeline section */}
      <Box position="relative" width="24px" mr={4}>
        {/* Vertical line */}
        {!isLast && (
          <Box
            position="absolute"
            top="0"
            left="50%"
            transform="translateX(-50%)"
            height="100%"
            width="2px"
            bg={timelineColor}
          />
        )}
        
        {/* Event icon */}
        <Tooltip label={item.status === InfraStatus.OK ? "Operational" : "Needs Attention"} placement="right">
          <Box
            position="absolute"
            top="0"
            left="50%"
            transform="translateX(-50%)"
            borderRadius="full"
            borderWidth="2px"
            borderColor={iconColor}
            bg={cardBg}
            p={1}
            boxSize="28px"
            display="flex"
            alignItems="center"
            justifyContent="center"
            zIndex={1}
            boxShadow="sm"
          >
            {item.status === InfraStatus.OK ? (
              <CheckCircle color={useColorModeValue("#3182CE", "#63B3ED")} size={16} />
            ) : (
              <AlertCircle color={useColorModeValue("#E53E3E", "#FC8181")} size={16} />
            )}
          </Box>
        </Tooltip>
      </Box>

      {/* Card content */}
      <Box flex={1} pb={isLast ? 0 : 6} width="100%">
        <Box
          bg={cardBg}
          borderRadius="lg"
          borderWidth="1px"
          borderColor={cardBorder}
          boxShadow="md"
          p={5}
          transition="all 0.2s"
          _hover={{ boxShadow: "lg", transform: "translateY(-2px)" }}
        >
          {/* Status badge */}
          <Badge 
            colorScheme={getEventStatusColor(item.eventStatus)} 
            mb={3} 
            fontSize="0.8em" 
            px={2} 
            py={1} 
            borderRadius="md"
          >
            {item.eventStatus}
          </Badge>

          {/* Title */}
          <Text fontWeight="bold" fontSize="md" color={textColor} mb={2}>
            {item.description}
          </Text>

          {/* Time information */}
          <Flex direction="column" gap={1} mb={3}>
            <Flex align="center">
              <Clock size={14} style={{ marginRight: "6px" }} color={useColorModeValue("#718096", "#A0AEC0")} />
              <Text fontSize="sm" color={subTextColor}>
                Start: {formatDate(item.dateCaptured)}
              </Text>
            </Flex>
            <Flex align="center">
              <Clock size={14} style={{ marginRight: "6px" }} color={useColorModeValue("#718096", "#A0AEC0")} />
              <Text fontSize="sm" color={subTextColor}>
                End: {item.endTime ? formatDate(item.endTime) : "Pending"}
              </Text>
            </Flex>
          </Flex>

          {/* Object status */}
          <Flex align="center" mb={4}>
            <Text fontSize="sm" color={subTextColor}>
              Status:
            </Text>
            <Badge 
              colorScheme={getStatusColor(item.status)} 
              ml={2}
              px={2}
              py={0.5}
              borderRadius="md"
            >
              {item.status}
            </Badge>
          </Flex>

          {/* Action buttons */}
          <Flex direction="column" gap={3}>
            {item.eventStatus === EventStatus.REPAIR && (
              <Button
                width="full"
                size="md"
                colorScheme="green"
                onClick={handleUpdateRepairEvent}
                isLoading={isLoading}
                loadingText="Updating..."
                leftIcon={<Check size={16} />}
                borderRadius="md"
                boxShadow="sm"
              >
                Mark as Complete
              </Button>
            )}

            {item.image?.pathUrl && (
              <HStack spacing={2}>
                <Button
                  size="md"
                  colorScheme="blue"
                  onClick={onImageModalOpen}
                  leftIcon={<ImageIcon size={14} />}
                  borderRadius="md"
                >
                  Image
                </Button>
                <Button
                  size="md"
                  colorScheme="purple"
                  onClick={onCheckEventPopupOpen}
                  leftIcon={<Video size={14} />}
                  borderRadius="md"
                >
                  Playback
                </Button>
              </HStack>
            )}
          </Flex>
        </Box>
      </Box>

      {/* Modals */}
      <ImageModal
        isOpen={isImageModalOpen}
        onClose={onImageModalClose}
        imageUrl={item.image?.pathUrl}
        title={item.description}
      />

      {schedule && (
        <CheckEventPopup
          isOpen={isCheckEventPopupOpen}
          onClose={onCheckEventPopupClose}
          videoUrl={schedule?.videoUrl}
          eventId={item.id}
        />
      )}

    </Flex>
  );
};

export default EventItem;