// Components.tsx (Reusable components)
import { forwardRef } from "react";
import {
  Box,
  Text,
  Stack,
  Button,
  Divider,
  Flex,
  Badge,
  Tooltip,
  Stat,
  StatLabel,
  StatNumber,
  StatGroup,
  HStack,
  FormControl,
  Select,
  FormLabel,
} from "@chakra-ui/react";
import { FilterProcessRequests, InfraObjectProcess } from "../../type/models";
import { useMap } from "react-leaflet";
import { useEffect } from "react";

// Component for automatically centering map on first object
export const MapAutoCenter: React.FC<{ infraObjects: InfraObjectProcess[] }> = ({
  infraObjects,
}) => {
  const map = useMap();

  useEffect(() => {
    if (infraObjects.length > 0) {
      const { latitude, longitude } = infraObjects[0];
      map.setView([latitude, longitude], 15);
      
      // Focus on first marker by simulating a click on it
      setTimeout(() => {
        const firstMarkerElement = document.querySelector('.leaflet-marker-icon');
        if (firstMarkerElement) {
          (firstMarkerElement as HTMLElement).click();
        }
      }, 500);
    }
  }, [infraObjects, map]);

  return null;
};


// Popup component for infrastructure objects
export const InfraPopup: React.FC<{
  infra: InfraObjectProcess;
  onGoToTime: (infra: InfraObjectProcess) => void;
  onAccept: (infra: InfraObjectProcess) => void;
  onReject: (infra: InfraObjectProcess) => void;
}> = ({ infra, onGoToTime, onAccept, onReject }) => {
  useEffect(() => {
    const handleKeydown = (event: KeyboardEvent) => {
      if (event.key === "a") {
        onAccept(infra);
      } else if (event.key === "s") {
        onReject(infra);
      } else if (event.key === "d") {
        onGoToTime(infra);
      }
    };

    window.addEventListener("keydown", handleKeydown);
    return () => window.removeEventListener("keydown", handleKeydown);
  }, [infra, onAccept, onReject, onGoToTime]);

  // Helper function to get badge color based on status
  const getStatusColor = (status: string) => {
    if (status === "OK") return "green";
    if (status === "NOT OK") return "red";
    if (status === "PENDING") return "orange";
    return "gray";
  };

  return (
    <Box borderRadius="lg" bg="white" maxW="400px" p={3} boxShadow="md">
      {/* Header */}
      <Flex justify="space-between" align="center" mb={2}>
        <Text fontSize="md" fontWeight="bold" color="gray.800">
          {infra.name}
        </Text>
        <HStack spacing={1}>
          <Badge colorScheme={getStatusColor(infra.status)}>{infra.status}</Badge>
          <Badge colorScheme={getStatusColor(infra.processStatus)}>{infra.processStatus}</Badge>
          <Badge colorScheme="blue">{infra.eventStatus}</Badge>
        </HStack>
      </Flex>
      
      <Flex justify="space-between" mb={2}>
        <Box flex="1">
          <Text fontSize="sm">Category: <b>{infra.category}</b></Text>
          <Text fontSize="sm">
            Location: {infra.latitude.toFixed(4)}, {infra.longitude.toFixed(4)}
          </Text>
          <Text fontSize="sm">Confidence: <b>{(infra.confidence * 100).toFixed(1)}%</b></Text>
          <Text fontSize="sm">Time: {new Date(infra.dateCaptured).toLocaleString()}</Text>
        </Box>
        
        {/* Image preview */}
        {infra?.image?.pathUrl && (
          <Box ml={2} width="100px" height="100px" bg="gray.100" borderRadius="md" overflow="hidden">
            <img
              src={infra.image.pathUrl}
              alt="Infra object"
              style={{
                width: "100%",
                height: "100%",
                objectFit: "cover",
              }}
            />
          </Box>
        )}
      </Flex>

      <Divider my={2} />

      {/* Action buttons */}
      <Stack direction="row" spacing={2} justify="center">
        <Button
          onClick={() => onAccept(infra)}
          colorScheme="green"
          size="sm"
          flex="1"
        >
          Accept (a)
        </Button>
        <Button
          onClick={() => onReject(infra)}
          colorScheme="red"
          size="sm"
          flex="1"
        >
          Reject (s)
        </Button>
        <Button
          onClick={() => onGoToTime(infra)}
          colorScheme="blue"
          size="sm"
          flex="1"
        >
          View (d)
        </Button>
      </Stack>
    </Box>
  );
};

// Video player component
export const VideoPlayer = forwardRef<HTMLVideoElement, { 
  videoUrl: string,
  infraObjects: InfraObjectProcess[]
}>(({ videoUrl, infraObjects }, ref) => (
  <Box w="100%" h="100%" position="relative" borderRadius="md" overflow="hidden" boxShadow="md">
    <video
      ref={ref}
      controls
      src={videoUrl}
      autoPlay
      style={{
        width: "100%",
        height: "100%",
        objectFit: "contain",
        backgroundColor: "black",
      }}
    />
  </Box>
));

// Stats component for displaying metrics
export const InfraStats: React.FC<{ infraObjects: InfraObjectProcess[] }> = ({ infraObjects }) => {
  // Calculate stats
  const totalObjects = infraObjects.length;
  const pendingCount = infraObjects.filter(obj => obj.processStatus === 'PENDING').length;
  const approvedCount = infraObjects.filter(obj => obj.processStatus === 'APPROVED').length;
  const rejectedCount = infraObjects.filter(obj => obj.processStatus === 'REJECTED').length;
  
  const okCount = infraObjects.filter(obj => obj.status === 'OK').length;
  const notOkCount = infraObjects.filter(obj => obj.status === 'NOT OK').length;
  
  const newCount = infraObjects.filter(obj => obj.eventStatus === 'NEW').length;
  const updatedCount = infraObjects.filter(obj => obj.eventStatus === 'UPDATED').length;

  return (
    <Box p={4} bg="white" borderRadius="md" boxShadow="sm">
      <Text fontWeight="bold" mb={3}>Infrastructure Statistics</Text>
      <Flex flexWrap="wrap" gap={4}>
        <StatGroup flex="1" minW="200px">
          <Stat>
            <StatLabel>Total Objects</StatLabel>
            <StatNumber>{totalObjects}</StatNumber>
          </Stat>
        </StatGroup>

        <StatGroup flex="1" minW="200px">
          <Stat>
            <StatLabel color="orange.500">Pending</StatLabel>
            <StatNumber>{pendingCount}</StatNumber>
          </Stat>
          <Stat>
            <StatLabel color="green.500">Approved</StatLabel>
            <StatNumber>{approvedCount}</StatNumber>
          </Stat>
          <Stat>
            <StatLabel color="red.500">Rejected</StatLabel>
            <StatNumber>{rejectedCount}</StatNumber>
          </Stat>
        </StatGroup>

        <StatGroup flex="1" minW="200px">
          <Stat>
            <StatLabel color="green.500">OK</StatLabel>
            <StatNumber>{okCount}</StatNumber>
          </Stat>
          <Stat>
            <StatLabel color="red.500">Not OK</StatLabel>
            <StatNumber>{notOkCount}</StatNumber>
          </Stat>
        </StatGroup>

        <StatGroup flex="1" minW="200px">
          <Stat>
            <StatLabel color="blue.500">New</StatLabel>
            <StatNumber>{newCount}</StatNumber>
          </Stat>
          <Stat>
            <StatLabel color="purple.500">Updated</StatLabel>
            <StatNumber>{updatedCount}</StatNumber>
          </Stat>
        </StatGroup>
      </Flex>
    </Box>
  );
};


// Filters component
export const FilterControls: React.FC<{
  filters: FilterProcessRequests;
  onFilterChange: (field: keyof FilterProcessRequests, value: string) => void;
  onProcessAll: () => void;
}> = ({ filters, onFilterChange, onProcessAll }) => {
  return (
    <HStack
      spacing={4}
      align="flex-end"
      py={4}
      px={2}
      bg="white"
      borderRadius="md"
      boxShadow="sm"
      wrap="wrap"
      justify="flex-start"
    >
      <FormControl maxW="160px">
        <FormLabel fontSize="sm" fontWeight="medium" color="gray.700">
          Status
        </FormLabel>
        <Select
          value={filters.status}
          onChange={(e) => onFilterChange("status", e.target.value)}
          placeholder="All Status"
          size="sm"
          bg="white"
          borderColor="gray.300"
          _focus={{
            borderColor: "blue.500",
            boxShadow: "0 0 0 1px blue.500",
          }}
        >
          <option value="OK">OK</option>
          <option value="NOT OK">NOT OK</option>
        </Select>
      </FormControl>

      <FormControl maxW="160px">
        <FormLabel fontSize="sm" fontWeight="medium" color="gray.700">
          Process Status
        </FormLabel>
        <Select
          value={filters.processStatus}
          onChange={(e) =>
            onFilterChange("processStatus", e.target.value)
          }
          placeholder="All"
          size="sm"
          bg="white"
          borderColor="gray.300"
          _focus={{
            borderColor: "blue.500",
            boxShadow: "0 0 0 1px blue.500",
          }}
        >
          <option value="PENDING">PENDING</option>
          <option value="APPROVED">APPROVED</option>
          <option value="REJECTED">REJECTED</option>
        </Select>
      </FormControl>

      <FormControl maxW="160px">
        <FormLabel fontSize="sm" fontWeight="medium" color="gray.700">
          Event Status
        </FormLabel>
        <Select
          value={filters.eventStatus}
          onChange={(e) =>
            onFilterChange("eventStatus", e.target.value)
          }
          placeholder="All Event Status"
          size="sm"
          bg="white"
          borderColor="gray.300"
          _focus={{
            borderColor: "blue.500",
            boxShadow: "0 0 0 1px blue.500",
          }}
        >
          <option value="NEW">NEW</option>
          <option value="UPDATED">UPDATED</option>
        </Select>
      </FormControl>

      <Tooltip
        label="Process all infrastructure objects in this schedule"
        placement="top"
      >
        <Button
          colorScheme="blue"
          size="sm"
          px={4}
          fontWeight="medium"
          onClick={onProcessAll}
          _hover={{ bg: "blue.600" }}
          _active={{ bg: "blue.700" }}
        >
          Process All
        </Button>
      </Tooltip>
    </HStack>
  );
};