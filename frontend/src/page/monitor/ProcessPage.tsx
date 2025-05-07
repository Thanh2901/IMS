// ProcessPage.tsx
import { useParams } from "react-router-dom";
import {
  useGetProcessFilterQuery,
  useProcessObjectMutation,
  useProcessScheduleMutation,
  useRejectProcessMutation,
} from "../../redux/service/infrastructure";
import { FilterProcessRequests, InfraObjectProcess } from "../../type/models";
import { useState, useRef, useEffect } from "react";
import {
  Box,
  Flex,
  Spinner,
  Center,
  Text,
  useDisclosure,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalCloseButton,
} from "@chakra-ui/react";
import { useGetScheduleQuery } from "../../redux/service/camera";
import {
  MapContainer,
  TileLayer,
  LayersControl,
  Marker,
  Popup,
  useMap,
} from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { getCustomIcon } from "../../type/utils";
import useCustomToast from "../../hook/useCustomToast";
import { 
  MapAutoCenter, 
  InfraPopup, 
  VideoPlayer, 
  InfraStats,
  FilterControls
} from "../../component/monitor/Process";

L.Icon.Default.mergeOptions({
  iconRetinaUrl:
    "https://unpkg.com/leaflet@1.9.3/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.9.3/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.3/dist/images/marker-shadow.png",
});

const ProcessPage: React.FC = () => {
  const { scheduleId } = useParams<{ scheduleId: string }>();
  const { data: schedule } = useGetScheduleQuery(scheduleId || "");
  const toast = useCustomToast();
  const [acceptProcess] = useProcessObjectMutation();
  const [rejectProcess] = useRejectProcessMutation();
  const [processSchedule] = useProcessScheduleMutation();
  const videoRef = useRef<HTMLVideoElement>(null);
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [selectedInfra, setSelectedInfra] = useState<InfraObjectProcess | null>(null);

  const {
    data: allProcessesData
  } = useGetProcessFilterQuery({ params: {
    scheduleId:scheduleId || "",
    status: "",
    processStatus: "",
    eventStatus: ""
  }});
  
  const [filters, setFilters] = useState<FilterProcessRequests>({
    scheduleId: scheduleId || "",
    status: "",
    processStatus: "PENDING",
    eventStatus: "",
  });

  // Get process filter query
  const {
    data: processFilterData,
    isLoading,
    error,
    refetch,
  } = useGetProcessFilterQuery({ params: filters });

  const infraObjects = processFilterData || [];

  // Handle actions
  const onAccept = async (infra: InfraObjectProcess) => {
    try {
      await acceptProcess(infra.id).unwrap();
      toast("Process Accepted", "Infra object has been accepted.", "success");
      refetch();
    } catch (error) {
      toast("Process Failed", "Infra object is not pending.", "error");
      console.error("Error accepting process:", error);
    }
  };

  const onReject = async (infra: InfraObjectProcess) => {
    try {
      await rejectProcess(infra.id).unwrap();
      toast("Process Rejected", "Infra object has been rejected.", "success");
      refetch();
    } catch (error) {
      console.error("Error rejecting process:", error);
    }
  };

  const handleProcessAll = async () => {
    try {
      await processSchedule(scheduleId || "").unwrap();
      toast("Process Schedule", "All infrastrucure objects have been processed.", "success");
      refetch();
    } catch (error) {
      console.error("Error processing schedule:", error);
    }
  };

  // Handle filter changes
  const handleFilterChange = (
    field: keyof FilterProcessRequests,
    value: string
  ) => {
    setFilters((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  // Handle video synchronization with infrastructure object click
  const handleInfraPointClick = (infra: InfraObjectProcess) => {
    if (infra.image?.frame > 0 && videoRef.current) {
      const offsetSeconds = infra.image.frame / 30 - 2; // Assuming 30 FPS, show 2s before
      if (offsetSeconds >= 0) {
        videoRef.current.currentTime = offsetSeconds;
        videoRef.current.play();
      }
    }
    // Set selected infra for detail view
    setSelectedInfra(infra);
    onOpen();
  };

  // Get video URL from schedule
  const videoUrl = schedule?.videoUrl || "";
  

  return (
    <Box p={4} bg="gray.50" minH="100vh">
      {/* Stats Component */}
      <InfraStats infraObjects={allProcessesData || []} />
      
      {/* Filter Controls */}
      <Box mt={4}>
        <FilterControls 
          filters={filters} 
          onFilterChange={handleFilterChange} 
          onProcessAll={handleProcessAll} 
        />
      </Box>

      {/* Loading State */}
      {isLoading && (
        <Center p={8} bg="white" borderRadius="md" boxShadow="sm" mt={4}>
          <Spinner size="xl" color="blue.500" thickness="4px" />
        </Center>
      )}

      {/* Error State */}
      {error && (
        <Center p={8} bg="white" borderRadius="md" boxShadow="sm" mt={4}>
          <Text color="red.500">
            Error loading infrastructure data. Please try again.
          </Text>
        </Center>
      )}

      {/* Video and Map Display */}
      {!isLoading && !error && (
        <Flex direction="row" h="calc(100vh - 280px)" gap={4} mt={4}>
          {/* Video Player - Fixed 30% width */}
          <Box w="30%" h="100%" bg="white" borderRadius="md" boxShadow="md" p={2}>
            <VideoPlayer videoUrl={videoUrl} ref={videoRef} infraObjects={infraObjects} />
          </Box>

          {/* Map View - Remaining 70% width */}
          <Box w="70%" h="100%" position="relative" borderRadius="md" overflow="hidden" boxShadow="md">
            <MapContainer
              center={[21.028511, 105.804817]} // Default center (Hanoi)
              zoom={15}
              style={{ height: "100%", width: "100%" }}
              scrollWheelZoom
            >
              <MapAutoCenter infraObjects={infraObjects} />
              <LayersControl position="topright">
                <LayersControl.BaseLayer name="Default">
                  <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                  />
                </LayersControl.BaseLayer>
                <LayersControl.BaseLayer name="Satellite">
                  <TileLayer
                    attribution="Google Maps"
                    url="https://mt1.google.com/vt/lyrs=s&x={x}&y={y}&z={z}"
                  />
                </LayersControl.BaseLayer>
                <LayersControl.BaseLayer checked name="Topographic">
                  <TileLayer
                    attribution="Google Maps"
                    url="https://mt1.google.com/vt/lyrs=p&x={x}&y={y}&z={z}"
                  />
                </LayersControl.BaseLayer>
              </LayersControl>
              
              {/* Map Markers */}
              {infraObjects.map((infra, index) => (
                <Marker
                  key={index}
                  position={[infra.latitude, infra.longitude]}
                  icon={getCustomIcon(infra.category)}
                >
                  <Popup>
                    <InfraPopup
                      infra={infra}
                      onAccept={onAccept}
                      onReject={onReject}
                      onGoToTime={handleInfraPointClick}
                    />
                  </Popup>
                </Marker>
              ))}
            </MapContainer>
          </Box>
        </Flex>
      )}

      {/* Detail Modal */}
      <Modal isOpen={isOpen} onClose={onClose} size="xl" isCentered>
        <ModalOverlay />
        <ModalContent borderRadius="md">
          <ModalHeader bg="blue.50" borderTopRadius="md">
            Infrastructure Details
          </ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            {selectedInfra && (
              <InfraPopup
                infra={selectedInfra}
                onAccept={(infra) => {
                  onAccept(infra);
                  onClose();
                }}
                onReject={(infra) => {
                  onReject(infra);
                  onClose();
                }}
                onGoToTime={handleInfraPointClick}
              />
            )}
          </ModalBody>
        </ModalContent>
      </Modal>
    </Box>
  );
};

export default ProcessPage;