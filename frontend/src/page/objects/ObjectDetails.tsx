import { useParams, useNavigate } from "react-router-dom";
import { useState, useMemo } from "react";
import {
  useGetEventsByObjectIdQuery,
  useGetHistoryByInfraIdQuery,
  useGetObjectByIdQuery,
} from "../../redux/service/infrastructure";
import {
  Box,
  Flex,
  Text,
  Image,
  Badge,
  Divider,
  Spinner,
  Grid,
  GridItem,
  Button,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
  Heading,
  HStack,
  VStack,
  useColorModeValue,
  Avatar,
} from "@chakra-ui/react";
import {
  MapPin,
  Calendar,
  Tag as TagIcon,
  Zap,
  Clock as ClockIcon,
  Camera,
  AlertCircle,
  CheckCircle,
  Eye,
  FileText,
  Info,
  TrendingUp,
  Activity,
  Tool,
  Archive,
} from "react-feather";
import {
  formatConfidence,
  formatDate,
  getEventStatusColor,
} from "../../type/utils";
import {
  InfraCategory,
  InfraStatus,
  EventStatus,
  Event,
  History,
} from "../../type/models";

const ObjectDetails = () => {
  const { objectId } = useParams<{ objectId: string }>();
  const navigate = useNavigate();
  const { data: obj, isLoading: isObjectLoading } = useGetObjectByIdQuery(
    objectId as string
  );
  const { data: events = [], isLoading: isEventsLoading } =
    useGetEventsByObjectIdQuery(objectId as string);
  const { data: histories = [], isLoading: isHistoriesLoading } =
    useGetHistoryByInfraIdQuery(objectId as string);
  const [selectedEvent, setSelectedEvent] = useState<Event | null>(null);
  const [selectedHistory, setSelectedHistory] = useState<History | null>(null);

  const borderColor = useColorModeValue("gray.200", "gray.700");
  const hoverBg = useColorModeValue("gray.50", "gray.700");
  const sortedEvents = events;
  const sortedHistories = histories;

  const getStatusColor = (status: InfraStatus) => {
    return status === InfraStatus.OK ? "green" : "red";
  };

  const getEventStatusIcon = (status: EventStatus) => {
    switch (status) {
      case EventStatus.NEW:
        return <AlertCircle size={16} />;
      case EventStatus.UPDATED:
        return <Activity size={16} />;
      case EventStatus.REPAIR:
        return <Tool size={16} />;
      case EventStatus.LOST:
        return <Archive size={16} />;
      default:
        return <Info size={16} />;
    }
  };

  const getCategoryIcon = (category: InfraCategory) => {
    switch (category) {
      case InfraCategory.SIGN:
        return <TagIcon size={20} />;
      case InfraCategory.ROAD:
        return <MapPin size={20} />;
      case InfraCategory.LAMP:
        return <Zap size={20} />;
      case InfraCategory.GUARDRAIL:
        return <Divider orientation="horizontal" w="20px" />;
      default:
        return null;
    }
  };

  const handleShowObjectOnMap = () => {
    navigate("/map", { state: { clickedObjectId: objectId } });
  };

  const handleViewSchedule = (scheduleId: string) => {
    navigate(`/schedule/${scheduleId}`);
  };

  const handleViewEventDetails = (eventId: string) => {
    navigate(`/events/${eventId}`);
  };

  useMemo(() => {
    if (sortedEvents.length > 0 && !selectedEvent) {
      setSelectedEvent(sortedEvents[0]);
    }
    if (sortedHistories.length > 0 && !selectedHistory) {
      setSelectedHistory(sortedHistories[0]);
    }
  }, [sortedEvents, selectedEvent, sortedHistories, selectedHistory]);

  if (isObjectLoading) {
    return (
      <Flex justify="center" align="center" h="100vh">
        <Spinner size="xl" color="blue.500" thickness="4px" />
      </Flex>
    );
  }

  if (!obj) {
    return (
      <Flex direction="column" justify="center" align="center" h="100vh">
        <AlertCircle size={48} color="red" />
        <Text fontSize="xl" mt={4}>
          Object not found
        </Text>
        <Button mt={4} onClick={() => navigate("/")}>
          Back to Home
        </Button>
      </Flex>
    );
  }

  return (
    <Box p={4} maxW="1600px" mx="auto">
      <Box mb={6}>
        <Flex justifyContent="space-between" alignItems="center" mb={4}>
          <HStack spacing={4} align="center">
            <Box bg={`${obj.category}.100`} p={2} borderRadius="md">
              {getCategoryIcon(obj.category)}
            </Box>
            <Text fontSize="2xl" fontWeight="bold">
              {obj.name}
            </Text>
            <Badge
              colorScheme={getStatusColor(obj.status)}
              fontSize="lg"
              px={4}
              py={2}
              borderRadius="full"
            >
              {obj.status}
            </Badge>
          </HStack>
          <Button
            leftIcon={<Eye size={16} />}
            colorScheme="blue"
            onClick={handleShowObjectOnMap}
          >
            View on Map
          </Button>
        </Flex>
        <Divider mb={6} />
      </Box>

      <Tabs variant="enclosed" colorScheme="blue" size="md" isLazy>
        <TabList>
          <Tab
            fontWeight="semibold"
            _selected={{ color: "blue.500", borderColor: "blue.500" }}
          >
            <HStack spacing={2}>
              <Info size={16} />
              <Text>Details</Text>
            </HStack>
          </Tab>
          <Tab
            fontWeight="semibold"
            _selected={{ color: "blue.500", borderColor: "blue.500" }}
          >
            <HStack spacing={2}>
              <FileText size={16} />
              <Text>Events</Text>
            </HStack>
            {events && events.length > 0 && (
              <Badge ml={2} colorScheme="blue" borderRadius="full">
                {events.length}
              </Badge>
            )}
          </Tab>
          <Tab
            fontWeight="semibold"
            _selected={{ color: "blue.500", borderColor: "blue.500" }}
          >
            <HStack spacing={2}>
              <ClockIcon size={16} />
              <Text>History</Text>
            </HStack>
            {histories && histories.length > 0 && (
              <Badge ml={2} colorScheme="blue" borderRadius="full">
                {histories.length}
              </Badge>
            )}
          </Tab>
        </TabList>

        <TabPanels>
          {/* Details Tab */}
          <TabPanel p={6}>
            <Grid templateColumns={{ base: "1fr", md: "1fr 1fr" }} gap={8}>
              {/* Images Section */}
              <Box>
                <VStack spacing={4}>
                  {/* Main Image */}
                  <Box w="100%">
                    <Text fontWeight="bold" mb={2}>
                      Main Image
                    </Text>
                    <Image
                      src={obj.image?.pathUrl}
                      alt={obj.name}
                      borderRadius="lg"
                      fallback={
                        <Box
                          height="200px"
                          bg="gray.100"
                          borderRadius="lg"
                          display="flex"
                          alignItems="center"
                          justifyContent="center"
                        >
                          <Camera size={48} color="gray" opacity={0.5} />
                        </Box>
                      }
                      maxH="200px"
                      w="100%"
                      background="black"
                      objectFit="contain"
                      border="1px solid"
                      borderColor="gray.200"
                    />
                  </Box>

                  {/* Additional Images */}
                  <Grid templateColumns="repeat(2, 1fr)" gap={4} w="100%">
                    {obj.info?.avatar && (
                      <Box>
                        <Text fontWeight="bold" mb={2}>
                          Avatar
                        </Text>
                        <Image
                          src={obj?.info?.avatar}
                          alt="Avatar"
                          borderRadius="lg"
                          fallback={
                            <Box
                              height="150px"
                              bg="gray.100"
                              borderRadius="lg"
                              display="flex"
                              alignItems="center"
                              justifyContent="center"
                            >
                              <Camera size={32} color="gray" opacity={0.5} />
                            </Box>
                          }
                          maxH="150px"
                          w="100%"
                          background="black"
                          objectFit="contain"
                          border="1px solid"
                          borderColor="gray.200"
                        />
                      </Box>
                    )}
                    {obj.info?.originalImage && (
                      <Box>
                        <Text fontWeight="bold" mb={2}>
                          Original Image
                        </Text>
                        <Image
                          src={obj.info.originalImage}
                          alt="Original Image"
                          borderRadius="lg"
                          fallback={
                            <Box
                              height="150px"
                              bg="gray.100"
                              borderRadius="lg"
                              display="flex"
                              alignItems="center"
                              justifyContent="center"
                            >
                              <Camera size={32} color="gray" opacity={0.5} />
                            </Box>
                          }
                          maxH="150px"
                          w="100%"
                          background="black"
                          objectFit="contain"
                          border="1px solid"
                          borderColor="gray.200"
                        />
                      </Box>
                    )}
                  </Grid>
                </VStack>
              </Box>

              {/* Information Section */}
              <Box overflowY="auto">
                <Heading as="h3" size="md" mb={4}>
                  Infrastructure Information
                </Heading>
                <VStack spacing={4} align="stretch">
                  <Grid templateColumns="repeat(2, 1fr)" gap={4}>
                    <GridItem>
                      <HStack spacing={2}>
                        <TagIcon size={16} color="gray" />
                        <Text fontWeight="bold" fontSize="sm">
                          Category
                        </Text>
                      </HStack>
                      <Text ml={6} mt={1} color="gray.700">
                        {obj.category}
                      </Text>
                    </GridItem>

                    <GridItem>
                      <HStack spacing={2}>
                        <Calendar size={16} color="gray" />
                        <Text fontWeight="bold" fontSize="sm">
                          Last Updated
                        </Text>
                      </HStack>
                      <Text ml={6} mt={1} color="gray.700">
                        {formatDate(obj.dateCaptured)}
                      </Text>
                    </GridItem>

                    <GridItem>
                      <HStack spacing={2}>
                        <MapPin size={16} color="gray" />
                        <Text fontWeight="bold" fontSize="sm">
                          Type
                        </Text>
                      </HStack>
                      <Text ml={6} mt={1} color="gray.700">
                        {obj.type}
                      </Text>
                    </GridItem>

                    <GridItem>
                      <HStack spacing={2}>
                        <Zap size={16} color="gray" />
                        <Text fontWeight="bold" fontSize="sm">
                          Confidence
                        </Text>
                      </HStack>
                      <Badge
                        ml={6}
                        mt={1}
                        colorScheme={obj.confidence > 80 ? "green" : "orange"}
                      >
                        {formatConfidence(obj.confidence)}%
                      </Badge>
                    </GridItem>

                    <GridItem>
                      <HStack spacing={2}>
                        <TrendingUp size={16} color="gray" />
                        <Text fontWeight="bold" fontSize="sm">
                          Level
                        </Text>
                      </HStack>
                      <Badge
                        ml={6}
                        mt={1}
                        colorScheme={
                          obj.level === 3
                            ? "green"
                            : obj.level === 2
                            ? "blue"
                            : obj.level === 1
                            ? "orange"
                            : "gray"
                        }
                        fontWeight={obj.level > 0 ? "bold" : "normal"}
                      >
                        {`Level ${obj.level}`}
                      </Badge>
                    </GridItem>

                    <GridItem>
                      <HStack spacing={2}>
                        <CheckCircle size={16} color="gray" />
                        <Text fontWeight="bold" fontSize="sm">
                          Updated
                        </Text>
                      </HStack>
                      <Badge
                        ml={6}
                        mt={1}
                        colorScheme={obj.isUpdated ? "green" : "red"}
                      >
                        {obj.isUpdated ? "✅ Updated" : "❌ Not updated"}
                      </Badge>
                    </GridItem>

                    {/* Coordinates */}
                    <GridItem colSpan={2}>
                      <HStack spacing={2}>
                        <MapPin size={16} color="gray" />
                        <Text fontWeight="bold" fontSize="sm">
                          Coordinates
                        </Text>
                      </HStack>
                      <Text ml={6} mt={1} color="gray.700">
                        Latitude: {obj.latitude.toFixed(6)}, Longitude:{" "}
                        {obj.longitude.toFixed(6)}
                      </Text>
                    </GridItem>

                    <GridItem colSpan={2}>
                      <HStack spacing={2}>
                        <MapPin size={16} color="gray" />
                        <Text fontWeight="bold" fontSize="sm">
                          Location
                        </Text>
                      </HStack>
                      <Text ml={6} mt={1} color="gray.700">
                        {obj.location || "N/A"}
                      </Text>
                    </GridItem>

                    {/* Additional InfraInfo */}
                    {obj.info && (
                      <>
                        <GridItem colSpan={2}>
                          <HStack spacing={2}>
                            <TagIcon size={16} color="gray" />
                            <Text fontWeight="bold" fontSize="sm">
                              Key ID
                            </Text>
                          </HStack>
                          <Text ml={6} mt={1} color="gray.700">
                            {obj.info.keyId}
                          </Text>
                        </GridItem>

                        <GridItem colSpan={2}>
                          <HStack spacing={2}>
                            <Calendar size={16} color="gray" />
                            <Text fontWeight="bold" fontSize="sm">
                              Created At
                            </Text>
                          </HStack>
                          <Text ml={6} mt={1} color="gray.700">
                            {formatDate(obj.info.createdAt)}
                          </Text>
                        </GridItem>

                        <GridItem colSpan={2}>
                          <HStack spacing={2}>
                            <TagIcon size={16} color="gray" />
                            <Text fontWeight="bold" fontSize="sm">
                              Management Unit
                            </Text>
                          </HStack>
                          <Text ml={6} mt={1} color="gray.700">
                            {obj.info.manageUnit}
                          </Text>
                        </GridItem>

                        <GridItem colSpan={2}>
                          <HStack spacing={2}>
                            <Text fontWeight="bold" fontSize="sm">
                              Additional Data
                            </Text>
                          </HStack>
                          <Text ml={6} mt={1} color="gray.700">
                            {obj.info.additionalData}
                          </Text>
                        </GridItem>
                      </>
                    )}
                  </Grid>
                </VStack>
              </Box>
            </Grid>
          </TabPanel>

          {/* Events Tab */}
          <TabPanel p={6}>
            {isEventsLoading ? (
              <Flex justify="center" align="center" h="300px">
                <Spinner size="xl" color="blue.500" thickness="4px" />
              </Flex>
            ) : !sortedEvents || sortedEvents.length === 0 ? (
              <Box textAlign="center" py={12}>
                <AlertCircle
                  size={48}
                  color="gray"
                  style={{ margin: "0 auto" }}
                />
                <Text color="gray.500" mt={4} fontSize="lg">
                  No events for this object
                </Text>
              </Box>
            ) : (
              <Flex direction={{ base: "column", lg: "row" }} gap={6}>
                {/* Events List */}
                <Box
                  flex="1"
                  borderWidth="1px"
                  borderColor={borderColor}
                  borderRadius="md"
                  overflow="hidden"
                >
                  <Box
                    bg="gray.50"
                    p={3}
                    borderBottomWidth="1px"
                    borderColor={borderColor}
                  >
                    <Text fontWeight="bold">Events</Text>
                  </Box>
                  <VStack
                    spacing={0}
                    align="stretch"
                    divider={<Divider />}
                    maxH="600px"
                    overflowY="auto"
                  >
                    {sortedEvents.map((event) => (
                      <Box
                        key={event.id}
                        p={4}
                        cursor="pointer"
                        bg={
                          selectedEvent?.id === event.id
                            ? hoverBg
                            : "transparent"
                        }
                        _hover={{ bg: hoverBg }}
                        onClick={() => setSelectedEvent(event)}
                        borderLeftWidth="4px"
                        borderLeftColor={
                          selectedEvent?.id === event.id
                            ? `${getEventStatusColor(event.eventStatus)}.500`
                            : "transparent"
                        }
                      >
                        <HStack
                          spacing={3}
                          align="flex-start"
                          justify="space-between"
                        >
                          <VStack spacing={0} align="flex-start">
                            <Text>{event.eventStatus}</Text>
                            <Text fontSize="sm" color="gray.500">
                              Start Time: {formatDate(event.dateCaptured)}
                            </Text>
                            <Text fontSize="sm" color="gray.500">
                              End Time:{" "}
                              {event.endTime
                                ? formatDate(event.endTime)
                                : "Now (Still ongoing)"}
                            </Text>
                          </VStack>
                          <Badge
                            fontSize="md"
                            px={3}
                            py={1}
                            colorScheme={getStatusColor(event.status)}
                            alignSelf="center"
                          >
                            {event.status}
                          </Badge>
                        </HStack>
                      </Box>
                    ))}
                  </VStack>
                </Box>

                {/* Event Details */}
                <Box
                  flex="1.2"
                  borderWidth="1px"
                  borderColor={borderColor}
                  borderRadius="md"
                  p={6}
                  maxH="600px"
                  overflowY="auto"
                >
                  {selectedEvent ? (
                    <Box>
                      <HStack
                        spacing={4}
                        mb={6}
                        align="center"
                        justify="space-between"
                      >
                        <HStack spacing={2} align="center">
                          <Box
                            p={2}
                            borderRadius="md"
                            bg={`${getEventStatusColor(
                              selectedEvent.eventStatus
                            )}.100`}
                          >
                            {getEventStatusIcon(selectedEvent.eventStatus)}
                          </Box>
                          <Text fontSize="xl" fontWeight="bold">
                            {selectedEvent.eventStatus}
                          </Text>
                        </HStack>
                        <Badge
                          fontSize="lg"
                          px={4}
                          py={2}
                          colorScheme={getStatusColor(selectedEvent.status)}
                        >
                          {selectedEvent.status}
                        </Badge>
                      </HStack>
                      <Grid templateColumns="repeat(2, 1fr)" gap={4}>
                        <GridItem>
                          <Text
                            fontSize="sm"
                            color="gray.500"
                            fontWeight="bold"
                          >
                            Time Detect
                          </Text>
                          <Text>{formatDate(selectedEvent.dateCaptured)}</Text>
                        </GridItem>
                        <GridItem>
                          <Text
                            fontSize="sm"
                            color="gray.500"
                            fontWeight="bold"
                          >
                            Event End Time
                          </Text>
                          <Text>
                            {selectedEvent.endTime
                              ? formatDate(selectedEvent.endTime)
                              : "Now (Still ongoing)"}
                          </Text>
                        </GridItem>
                        <GridItem>
                          <Text
                            fontSize="sm"
                            color="gray.500"
                            fontWeight="bold"
                          >
                            Status
                          </Text>
                          <Text>{selectedEvent.status}</Text>
                        </GridItem>
                        <GridItem>
                          <Button
                            leftIcon={<Eye size={16} />}
                            colorScheme="blue"
                            onClick={() =>
                              handleViewEventDetails(selectedEvent.id)
                            }
                          >
                            View Event Details
                          </Button>
                        </GridItem>
                        <GridItem>
                          <Text
                            fontSize="sm"
                            color="gray.500"
                            fontWeight="bold"
                          >
                            Evaluation level
                          </Text>
                          <Badge
                            colorScheme={
                              selectedEvent.level > 7
                                ? "red"
                                : selectedEvent.level > 4
                                ? "orange"
                                : "yellow"
                            }
                          >
                            Level {selectedEvent.level}
                          </Badge>
                        </GridItem>
                        <GridItem>
                          <Text
                            fontSize="sm"
                            color="gray.500"
                            fontWeight="bold"
                          >
                            Confidence
                          </Text>
                          <Badge
                            colorScheme={
                              selectedEvent.confidence > 80 ? "green" : "orange"
                            }
                          >
                            {formatConfidence(selectedEvent.confidence)}%
                          </Badge>
                        </GridItem>
                      </Grid>

                      {selectedEvent.image && (
                        <Box mt={6}>
                          <Text
                            fontSize="sm"
                            fontWeight="bold"
                            color="gray.500"
                            mb={2}
                          >
                            Image
                          </Text>
                          <Box
                            border="1px solid"
                            borderColor="gray.200"
                            borderRadius="md"
                            p={2}
                            bg="gray.50"
                            position="relative"
                          >
                            <Image
                              src={selectedEvent.image.pathUrl}
                              alt="Event"
                              borderRadius="md"
                              maxH="300px"
                              objectFit="contain"
                              width="100%"
                              fallback={
                                <Flex
                                  height="300px"
                                  align="center"
                                  justify="center"
                                  bg="gray.100"
                                  borderRadius="md"
                                >
                                  <Camera
                                    size={32}
                                    color="gray"
                                    opacity={0.5}
                                  />
                                </Flex>
                              }
                            />
                          </Box>
                        </Box>
                      )}
                    </Box>
                  ) : (
                    <Flex justify="center" align="center" h="100%">
                      <Text color="gray.500">
                        Select an event to view details
                      </Text>
                    </Flex>
                  )}
                </Box>
              </Flex>
            )}
          </TabPanel>

          {/* History Tab */}
          <TabPanel p={6}>
            {isHistoriesLoading ? (
              <Flex justify="center" align="center" h="300px">
                <Spinner size="xl" color="blue.500" thickness="4px" />
              </Flex>
            ) : !sortedHistories || sortedHistories.length === 0 ? (
              <Box textAlign="center" py={12}>
                <ClockIcon
                  size={48}
                  color="gray"
                  style={{ margin: "0 auto" }}
                />
                <Text color="gray.500" mt={4} fontSize="lg">
                  No history found for this object
                </Text>
              </Box>
            ) : (
              <Flex direction={{ base: "column", lg: "row" }} gap={6}>
                {/* History List */}
                <Box
                  flex="1"
                  borderWidth="1px"
                  borderColor={borderColor}
                  borderRadius="md"
                  overflow="hidden"
                >
                  <Box
                    bg="gray.50"
                    p={3}
                    borderBottomWidth="1px"
                    borderColor={borderColor}
                  >
                    <Text fontWeight="bold">History</Text>
                  </Box>
                  <VStack
                    spacing={0}
                    align="stretch"
                    divider={<Divider />}
                    maxH="600px"
                    overflowY="auto"
                  >
                    {sortedHistories.map((history) => (
                      <Box
                        key={history.id}
                        p={4}
                        cursor="pointer"
                        bg={
                          selectedHistory?.id === history.id
                            ? hoverBg
                            : "transparent"
                        }
                        _hover={{ bg: hoverBg }}
                        onClick={() => setSelectedHistory(history)}
                        borderLeftWidth="4px"
                        borderLeftColor={
                          selectedHistory?.id === history.id
                            ? `${getStatusColor(history.status)}.500`
                            : "transparent"
                        }
                      >
                        <HStack spacing={3} mb={2}>
                          <Avatar
                            size="sm"
                            bg={`${getStatusColor(history.status)}.100`}
                            color={`${getStatusColor(history.status)}.500`}
                            icon={<ClockIcon size={16} />}
                          />
                          <VStack spacing={0} align="flex-start">
                            <HStack spacing={2}>
                              <Badge
                                colorScheme={getStatusColor(history.status)}
                              >
                                {history.status}
                              </Badge>
                            </HStack>
                            <Text fontSize="sm" color="gray.500">
                              Detect Time: {formatDate(history.dateCaptured)}
                            </Text>
                          </VStack>
                        </HStack>
                      </Box>
                    ))}
                  </VStack>
                </Box>

                {/* History Details */}
                <Box
                  flex="1.2"
                  borderWidth="1px"
                  borderColor={borderColor}
                  borderRadius="md"
                  p={6}
                  maxH="600px"
                  overflowY="auto"
                >
                  {selectedHistory ? (
                    <Box>
                      <Flex
                        justifyContent="space-between"
                        alignItems="center"
                        mb={4}
                      >
                        <Badge
                          colorScheme={getStatusColor(selectedHistory.status)}
                          px={4}
                          py={2}
                          fontSize="md"
                        >
                          {selectedHistory.status}
                        </Badge>
                      </Flex>

                      <Grid templateColumns="repeat(2, 1fr)" gap={4} mb={6}>
                        <GridItem>
                          <Text
                            fontSize="sm"
                            fontWeight="bold"
                            color="gray.500"
                          >
                            Detect Time
                          </Text>
                          <Text>
                            {formatDate(selectedHistory.dateCaptured)}
                          </Text>
                        </GridItem>

                        <GridItem>
                          <Button
                            leftIcon={<Eye size={16} />}
                            colorScheme="blue"
                            onClick={() =>
                              handleViewSchedule(selectedHistory.scheduleId)
                            }
                          >
                            View Schedule
                          </Button>
                        </GridItem>

                        <GridItem>
                          <Text
                            fontSize="sm"
                            fontWeight="bold"
                            color="gray.500"
                          >
                            Severity Level
                          </Text>
                          <Badge
                            colorScheme={
                              selectedHistory.level > 7
                                ? "red"
                                : selectedHistory.level > 4
                                ? "orange"
                                : "yellow"
                            }
                          >
                            Level {selectedHistory.level}
                          </Badge>
                        </GridItem>

                        <GridItem>
                          <Text
                            fontSize="sm"
                            fontWeight="bold"
                            color="gray.500"
                          >
                            Confidence
                          </Text>
                          <Badge
                            colorScheme={
                              selectedHistory.confidence > 80
                                ? "green"
                                : "orange"
                            }
                          >
                            {formatConfidence(selectedHistory.confidence)}%
                          </Badge>
                        </GridItem>
                      </Grid>

                      <Divider my={4} />

                      {selectedHistory.image && (
                        <Box width="100%">
                          <Text
                            fontSize="sm"
                            fontWeight="bold"
                            color="gray.500"
                            mb={2}
                          >
                            Image
                          </Text>
                          <Image
                            src={selectedHistory.image.pathUrl}
                            alt="History"
                            borderRadius="md"
                            maxH="300px"
                            objectFit="contain"
                            width="100%"
                            fallback={
                              <Flex
                                height="300px"
                                align="center"
                                justify="center"
                                bg="gray.100"
                                borderRadius="md"
                              >
                                <Camera size={32} color="gray" opacity={0.5} />
                              </Flex>
                            }
                          />
                        </Box>
                      )}
                    </Box>
                  ) : (
                    <Flex justify="center" align="center" h="100%">
                      <Text color="gray.500">
                        Select a history entry to view details
                      </Text>
                    </Flex>
                  )}
                </Box>
              </Flex>
            )}
          </TabPanel>
        </TabPanels>
      </Tabs>
    </Box>
  );
};

export default ObjectDetails;
