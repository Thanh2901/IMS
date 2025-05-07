import { useNavigate, useParams } from "react-router-dom";
import { useGetEventByIdQuery } from "../../redux/service/infrastructure";
import {
  Box,
  Container,
  Heading,
  Text,
  Stack,
  Flex,
  Badge,
  Image,
  Spinner,
  Alert,
  AlertIcon,
  AlertTitle,
  AlertDescription,
  Tag,
  useColorModeValue,
  Button,
  HStack,
  VStack,
  Icon,
  Grid,
  GridItem,
} from "@chakra-ui/react";
import {
  Calendar,
  Clock,
  MapPin,
  Camera,
  AlertTriangle,
  Info,
  BarChart2,
  Check,
  X,
  Eye,
} from "react-feather";
import { EventStatus, InfraCategory, InfraStatus } from "../../type/models";
import { formatConfidence, formatDate } from "../../type/utils";

const EventDetails = () => {
  const navigate = useNavigate();
  const { eventId } = useParams<{ eventId: string }>();
  const { data: event, isLoading, isError } = useGetEventByIdQuery(eventId as string);

  const bgColor = useColorModeValue("white", "gray.800");
  const borderColor = useColorModeValue("gray.100", "gray.700");
  const headerBg = useColorModeValue("blue.50", "blue.900");
  const sectionBg = useColorModeValue("gray.50", "gray.700");

  const getStatusColor = (status: InfraStatus) => {
    return status === "OK" ? "green" : "red";
  };

  const getCategoryIcon = (category: InfraCategory) => {
    switch (category) {
      case InfraCategory.SIGN:
        return Info;
      case InfraCategory.ROAD:
        return MapPin;
      case InfraCategory.LAMP:
        return AlertTriangle;
      case InfraCategory.GUARDRAIL:
        return BarChart2;
      default:
        return Info;
    }
  };

  if (isLoading) {
    return (
      <Flex justify="center" align="center" height="50vh">
        <Spinner size="xl" thickness="4px" color="blue.500" />
      </Flex>
    );
  }

  if (isError || !event) {
    return (
      <Alert
        status="error"
        variant="subtle"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        textAlign="center"
        height="200px"
        borderRadius="md"
        mt={5}
      >
        <AlertIcon boxSize={10} mr={0} />
        <AlertTitle mt={4} mb={1} fontSize="lg">
          {isError ? "Error Loading Event" : "Event Not Found"}
        </AlertTitle>
        <AlertDescription maxWidth="sm">
          {isError
            ? "We couldn't load the event details. Please try again later."
            : "The event you're looking for could not be found."}
        </AlertDescription>
      </Alert>
    );
  }

  const handleShowObjectOnMap = () => {
    navigate("/map", { state: { clickedObjectId: event.infraObject?.id } });
  };

  const handleShowObjectDetails = () => {
    navigate(`/objects/${event.infraObject.id}`);
  };

  return (
    <Container maxW="container.xl" py={6}>
      {/* Page Header */}
      <Flex 
        justifyContent="space-between" 
        alignItems="center" 
        mb={6}
        pb={4}
        borderBottom="1px" 
        borderColor={borderColor}
      >
        <Heading size="lg" fontWeight="600">Event Details</Heading>
        <HStack spacing={3}>
          <Badge
            colorScheme={getStatusColor(event.status)}
            fontSize="md"
            py={1}
            px={3}
            borderRadius="full"
          >
            {event.status}
          </Badge>
          <Badge
            colorScheme={event.eventStatus === EventStatus.NEW ? "blue" : "orange"}
            fontSize="md"
            py={1}
            px={3}
            borderRadius="full"
          >
            {event.eventStatus}
          </Badge>
        </HStack>
      </Flex>

      {/* Main Content - Vertical Split */}
      <Grid templateColumns={{ base: "1fr", lg: "1fr 1fr" }} gap={6}>
        {/* Left Column - Event Details */}
        <GridItem>
          <Box 
            bg={bgColor} 
            borderRadius="xl" 
            boxShadow="sm" 
            overflow="hidden"
            border="1px"
            borderColor={borderColor}
            height="100%"
          >
            <Flex direction="column" height="100%">
              {/* Event Image */}
              <Box flex="1" minHeight="300px" position="relative">
                <Image
                  src={event.image?.pathUrl}
                  alt="Event Image"
                  objectFit="cover"
                  width="100%"
                  height="100%"
                  fallbackSrc="https://placehold.co/600x400/"
                />
              </Box>
              
              {/* Event Information */}
              <Box p={5} bg={bgColor}>
                <VStack spacing={4} align="stretch">
                  <InfoItem 
                    icon={Calendar}
                    label="Date Captured"
                    value={formatDate(event.dateCaptured)}
                  />
                  <InfoItem 
                    icon={Clock}
                    label="End Time"
                    value={formatDate(event.endTime)}
                  />
                  <InfoItem 
                    icon={AlertTriangle}
                    label="Severity Level"
                    value={
                      <Tag
                        size="md"
                        colorScheme={event.level > 7 ? "red" : event.level > 4 ? "orange" : "green"}
                        px={3}
                        py={1}
                      >
                        Level {event.level}
                      </Tag>
                    }
                  />
                  <InfoItem 
                    icon={BarChart2}
                    label="Confidence"
                    value={
                      <Tag
                        size="md"
                        colorScheme={
                          event.confidence > 0.7 ? "green" : event.confidence > 0.4 ? "orange" : "red"
                        }
                        px={3}
                        py={1}
                      >
                        {formatConfidence(event.confidence)}%
                      </Tag>
                    }
                  />
                  <InfoItem 
                    icon={event.status === "OK" ? Check : X}
                    iconColor={event.status === "OK" ? "green.500" : "red.500"}
                    label="Status"
                    value={event.status}
                  />
                </VStack>
              </Box>
            </Flex>
          </Box>
        </GridItem>

        {/* Right Column - Object Details */}
        <GridItem>
          <Box 
            bg={bgColor} 
            borderRadius="xl" 
            boxShadow="sm" 
            overflow="hidden"
            border="1px"
            borderColor={borderColor}
            height="100%"
          >
            <Flex direction="column" height="100%">
              {/* Object Header */}
              <Box p={5} borderBottom="1px" borderColor={borderColor}>
                <Flex justifyContent="space-between" alignItems="center">
                  <Heading size="md" fontWeight="500">Infrastructure Object</Heading>
                  <Button 
                    leftIcon={<Icon as={Eye} />} 
                    colorScheme="blue" 
                    size="sm"
                    variant="outline"
                    onClick={handleShowObjectDetails}
                  >
                    View Details
                  </Button>
                </Flex>
              </Box>

              {/* Object Image */}
              <Box flex="1" minHeight="250px" position="relative">
                <Image
                  src={event.infraObject?.image?.pathUrl}
                  alt="Infrastructure Object"
                  objectFit="cover"
                  width="100%"
                  height="100%"
                  fallbackSrc="https://placehold.co/600x400/"
                />
              </Box>
              
              {/* Object Information */}
              <Box p={5} bg={bgColor}>
                <VStack spacing={4} align="stretch">
                  <InfoItem 
                    icon={Info}
                    label="Name"
                    value={event.infraObject?.name || "N/A"}
                  />
                  <InfoItem 
                    icon={getCategoryIcon(event.infraObject?.category)}
                    label="Category"
                    value={
                      <Tag colorScheme="blue" textTransform="capitalize">
                        {event.infraObject?.category || "Unknown"}
                      </Tag>
                    }
                  />
                  <InfoItem 
                    icon={Camera}
                    label="Camera ID"
                    value={event.infraObject?.cameraId || "N/A"}
                  />
                  <InfoItem 
                    icon={MapPin}
                    label="Location"
                    value={event.infraObject?.location || "Unknown"}
                  />
                  <InfoItem 
                    icon={MapPin}
                    label="Coordinates"
                    value={
                      event.infraObject?.latitude && event.infraObject?.longitude
                        ? `${event.infraObject.latitude}, ${event.infraObject.longitude}`
                        : "N/A"
                    }
                  />
                </VStack>
              </Box>
            </Flex>
          </Box>
        </GridItem>
      </Grid>
    </Container>
  );
};

const InfoItem = ({ 
  icon, 
  iconColor = "blue.500", 
  label, 
  value 
}: { 
  icon: any; 
  iconColor?: string;
  label: string; 
  value: React.ReactNode; 
}) => (
  <HStack spacing={3} align="center" p={3} bg={useColorModeValue("gray.50", "gray.700")} borderRadius="md">
    <Icon as={icon} boxSize={5} color={iconColor} />
    <VStack spacing={0} align="start" width="100%">
      <Text fontSize="sm" color="gray.500">
        {label}
      </Text>
      <Box>
        {typeof value === 'string' ? (
          <Text fontWeight="medium">{value}</Text>
        ) : (
          value
        )}
      </Box>
    </VStack>
  </HStack>
);

export default EventDetails;