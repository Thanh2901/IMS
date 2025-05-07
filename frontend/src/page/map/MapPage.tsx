import { useState, useEffect } from "react";
import { 
  Box, Flex, Spinner, IconButton, Text, 
  Card, CardHeader, CardBody, Heading, Badge, Tooltip,
  useColorModeValue, Grid, GridItem, Icon, Image,
  useBoolean,
  Button,
  filter
} from "@chakra-ui/react";
import { ChevronLeft, ChevronRight, Map, List, Filter, RefreshCw, Layers, PlusSquare } from "react-feather";
import { useGetObjectsQuery } from "../../redux/service/infrastructure";
import { useGetCamerasQuery } from "../../redux/service/camera";
import ObjectsMap from "../../component/map/ObjectsMap";
import ObjectsFilter from "../../component/map/ObjectsFilter";
import EventsListBar from "../../component/map/EventListBar";
import Pagination from "../../component/common/Pagination";
import SearchBar from "../../component/map/SearchBar";
import { InfraObject, InfraStatus, ObjectFilterRequest } from "../../type/models";
import ObjectsLegend from "../../component/map/ObjectLegend";
import { formatDate } from "../../type/utils";

const MapPage = () => {
  const [objects, setObjects] = useState<InfraObject[]>([]);
  const [currentPage, setCurrentPage] = useState<number>(0);
  const [selectedCameraId, setSelectedCameraId] = useState<string | null>(null);
  const [selectedObjectId, setSelectedObjectId] = useState<string | null>(null);
  const [filterCriteria, setFilterCriteria] = useState<ObjectFilterRequest>({
    name: null,
    location: null,
    status: null,
    category: null,
    dateCaptured: null,
    cameraId: null,
    keyword: null,
    type: null,
    keyId: null,
    isPaged: false,
    page: 0,
    size: 10,
  });
  const [showEventsBar, setShowEventsBar] = useState<boolean>(false);
  const [showFilterPanel, setShowFilterPanel] = useState<boolean>(false);
  const [viewMode, setViewMode] = useState<'map' | 'list'>('map');
  const [isAddingObject, setIsAddingObject] = useBoolean(false);

  const bgColor = useColorModeValue("white", "gray.800");
  const borderColor = useColorModeValue("gray.200", "gray.700");
  const panelBg = useColorModeValue("gray.50", "gray.900");
  const textColor = useColorModeValue("gray.800", "white");
  const secondaryTextColor = useColorModeValue("gray.600", "gray.400");
  const accentColor = "blue.500";
  const hoverBg = useColorModeValue("gray.50", "gray.700");
  const selectedBg = useColorModeValue("blue.50", "blue.900");

  const { data: cameras, isLoading: isCameraLoading } = useGetCamerasQuery();
  const { data: fetchedObjects, isLoading } = useGetObjectsQuery({
    requestBody: {
      ...filterCriteria,
      page: currentPage,
    },
  });

  useEffect(() => {
    console.log("Filter criteria: ", filterCriteria);
  }, [filterCriteria]);

  useEffect(() => {
    if (fetchedObjects) {
      setObjects(fetchedObjects.pageData);
    }
  }, [fetchedObjects]);

  // useEffect(() => {
  //   if (cameras && cameras.length > 0 && !selectedCameraId) {
  //     const firstCameraId = cameras[0].id;
  //     setSelectedCameraId(firstCameraId);
  //     setFilterCriteria((prevCriteria) => ({
  //       ...prevCriteria,
  //       cameraId: firstCameraId,
  //     }));
  //   }
  // }, [cameras]);

  const handleClearFilters = () => {
    setFilterCriteria({
      name: null,
      location: null,
      status: null,
      dateCaptured: null,
      category: null,
      cameraId: null,
      keyword: null,
      type: null,
      keyId: null,
      page: 0,
      size: 100000,
    });
    setSelectedCameraId(null);
    setSelectedObjectId(null);
    setCurrentPage(0);
  };

  // const handleCameraSelect = (cameraId: string | null) => {
  //   setSelectedCameraId(cameraId);
  //   setFilterCriteria((prevCriteria) => ({
  //     ...prevCriteria,
  //     cameraId: cameraId,
  //   }));
  // };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const toggleEventsBar = () => {
    setShowEventsBar((prev) => !prev);
  };

  const toggleFilterPanel = () => {
    setShowFilterPanel((prev) => !prev);
  };

  const handleObjectSelect = (object: InfraObject) => {
    setSelectedObjectId(object.id);
    setShowEventsBar(true);
  };

  if (isLoading || isCameraLoading) {
    return (
      <Flex height="100vh" width="100%" justifyContent="center" alignItems="center" flexDirection="column" gap={4}>
        <Spinner size="xl" color={accentColor} thickness="4px" speed="0.65s" />
        <Text>Loading insights data...</Text>
      </Flex>
    );
  }

  // const totalPages = fetchedObjects?.totalPages || 1;

  const hasActiveFilters = Object.values(filterCriteria).some(value => 
    value !== null && value !== '' && !(Array.isArray(value) && value.length === 0)
  );

  const renderObjectsList = () => (
    <Box width="100%" height="100%" bg={bgColor} borderRadius="md" boxShadow="sm" overflow={"auto"}>
      <Flex

        bg={panelBg}
        borderBottomWidth="1px"
        borderColor={borderColor}
        align="center"
        justify="space-between"
      >
        <Flex align="center" gap={2}>
          <Icon as={Layers} color={accentColor} />
          <Heading size="md">Objects List</Heading>
        </Flex>
        <Text color={secondaryTextColor} fontSize="sm">
          Showing {objects.length} of {fetchedObjects?.totalElements || 0} objects
        </Text>
      </Flex>

      <Box
        height={"full"}
      >
        {objects.length > 0 ? (
          objects.map((obj) => (
            <Flex
              key={obj.id}
              p={4}
              borderBottom="1px"
              borderColor={borderColor}
              bg={selectedObjectId === obj.id ? selectedBg : "transparent"}
              onClick={() => handleObjectSelect(obj)}
              cursor="pointer"
              _hover={{ bg: hoverBg }}
              transition="background 0.2s"
              gap={4}
              alignItems="center"
            >
              <Box
                position="relative"
                minWidth="60px"
                borderRadius="md"
                overflow="hidden"
                boxShadow="sm"
              >
                {
                  obj.image?.pathUrl ? 
                  <Image
                    src={obj.image.pathUrl}
                    boxSize="60px"
                    objectFit="cover"
                    alt={obj.name}
                    transition="transform 0.2s"
                    _hover={{ transform: "scale(1.05)" }}
                  /> : (
                    <Text fontWeight={"semibold"} color={textColor}>N/A</Text>
                  )
                }
              </Box>

              <Box flex={1}>
                <Flex alignItems="center" justifyContent="space-between" mb={1}>
                  <Text fontWeight="bold" color={textColor}>
                    {obj.name || `Object ${obj.id.slice(0, 8)}`}
                  </Text>
                  <Badge
                    colorScheme={obj.status === InfraStatus.OK ? "green" : "red"}
                    px={2}
                    py={1}
                    borderRadius="full"
                  >
                    {obj.status}
                  </Badge>
                </Flex>
                <Grid gap={1}>
                  <Text fontSize="sm" color={secondaryTextColor}>
                    <Text as="span" fontWeight="medium">Category:</Text> {obj.category}
                  </Text>
                  <Text fontSize="sm" color={secondaryTextColor}>
                    <Text as="span" fontWeight="medium">Location:</Text> {obj.location}
                  </Text>
                  <Text fontSize="sm" color={secondaryTextColor}>
                    <Text as="span" fontWeight="medium">Confidence:</Text> {(obj.confidence * 100).toFixed(1)}%
                  </Text>
                </Grid>
                <Text fontSize="xs" color={secondaryTextColor} mt={1}>
                  Captured: {formatDate(obj.dateCaptured)}
                </Text>
              </Box>
            </Flex>
          ))
        ) : (
          <Flex p={6} justifyContent="center" alignItems="center" height="200px" direction="column">
            <Text mb={4}>No objects found matching your criteria</Text>
            <IconButton
              aria-label="Clear filters"
              icon={<RefreshCw size={16} />}
              onClick={handleClearFilters}
              size="sm"
              colorScheme="blue"
            />
          </Flex>
        )}
      </Box>
    </Box>
  );

  return (
    <Flex p={0} height="100%" width="100%" gap={6}>
      {/* Left sidebar for events */}
      <Flex
        width={showEventsBar ? "350px" : "auto"}
        transition="all 0.3s ease"
        position="relative"
      >
        {showEventsBar && (
          <Card borderColor={borderColor} width="full" overflow="hidden">
            <CardHeader bg={"gray.50"} p={3}>
              <Heading size="sm">Event Details</Heading>
            </CardHeader>
            <CardBody p={0}>
              <EventsListBar objectId={selectedObjectId || ""} />
            </CardBody>
          </Card>
        )}
        <IconButton
          aria-label="Toggle Events Bar"
          icon={showEventsBar ? <ChevronLeft size={18} /> : <ChevronRight size={18} />}
          onClick={toggleEventsBar}
          position="absolute"
          right={showEventsBar ? "-10px" : "auto"}
          left={!showEventsBar ? "-10px" : "auto"}
          top="50%"
          transform="translateY(-50%)"
          zIndex={2}
          size="sm"
          colorScheme="blue"
          borderRadius="full"
          boxShadow="md"
        />
      </Flex>

      {/* Main content area */}
      <Flex flex={1} direction="column" overflow="hidden">
        {/* Top bar with controls */}
        <Card mb={4} borderColor={borderColor}>
          <CardBody p={3}>
            <Grid templateColumns="5fr 1fr" gap={4}>
              <GridItem>
                <Flex>
                  <SearchBar
                    onObjectSelect={handleObjectSelect}
                    onSetObjects={(newObjects) => {
                      setObjects(newObjects);
                      setCurrentPage(0);
                    }}
                    defaultObjects={fetchedObjects?.pageData || []}
                  />
                </Flex>
              </GridItem>
              <GridItem>
                <Flex justifyContent="flex-end" gap={2}>
                  <Tooltip label="Toggle Filters">
                    <Button
                      aria-label="Toggle Filters"
                      leftIcon={<Filter size={18} />}
                      onClick={toggleFilterPanel}
                      colorScheme={hasActiveFilters ? "blue" : "gray"}
                      variant={hasActiveFilters ? "solid" : "outline"}
                    >
                      Filters
                    </Button>
                  </Tooltip>
                  <Tooltip label="Switch View">
                    <Button
                      aria-label="Switch View"
                      leftIcon={viewMode === 'map' ? <List size={18} /> : <Map size={18} />}
                      onClick={() => setViewMode(viewMode === 'map' ? 'list' : 'map')}
                      variant="outline"
                    >
                      View
                    </Button>
                  </Tooltip>
                  <Tooltip label="Adding Object">
                    <Button
                      aria-label="Toggle Adding Object"
                      leftIcon={<PlusSquare size={18} />}
                      onClick={setIsAddingObject.toggle}
                      colorScheme={isAddingObject ? "blue" : "gray"}
                      variant={"solid"}
                    >
                      Adding Object
                    </Button>
                  </Tooltip>
                </Flex>
              </GridItem>
            </Grid>
          </CardBody>
        </Card>

        {/* Filter panel - collapsible */}
        {showFilterPanel && (
          <Card mb={2} borderColor={borderColor}>
            <CardBody p={3}>
              <Flex justifyContent="space-between" alignItems="center">
                <ObjectsFilter
                  filterCriteria={filterCriteria}
                  setFilterCriteria={setFilterCriteria}
                  onClear={handleClearFilters}
                  cameras={cameras || []}
                />
              </Flex>
            </CardBody>
          </Card>
        )}

        {/* Main content - Map or List view */}
        {viewMode === 'map' ? (
          <Box height="full" width="full">
            <ObjectsMap
              isAddingObject={isAddingObject}
              selectedObjectId={selectedObjectId}
              onSelect={setSelectedObjectId}
              objects={objects}
              showEventsBar={() => setShowEventsBar(true)}
            />
          </Box>
        ) : (
          renderObjectsList()
        )}

        <Flex justify="center" mt={2} align="center">
          <ObjectsLegend />
        </Flex>
      </Flex>

    </Flex>
  );
};

export default MapPage;