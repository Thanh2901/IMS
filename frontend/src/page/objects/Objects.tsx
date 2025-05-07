import React, { useEffect, useState } from "react";
import { 
  Box, 
  VStack, 
  Flex, 
  Input, 
  Select, 
  Button, 
  Spinner, 
  Text,
  HStack,
  Icon as ChakraIcon,
  useDisclosure
} from "@chakra-ui/react";
import { 
  Camera as CameraIcon, 
  Search, 
  X, 
  Layers,
  Box as BoxIcon,
  AlertTriangle
} from "react-feather";
import { useGetObjectsQuery } from "../../redux/service/infrastructure";
import { useGetCamerasQuery } from "../../redux/service/camera";
import ObjectTable from "../../component/objects/ObjectTable";
import Pagination from "../../component/common/Pagination";
import { InfraType, ObjectFilterRequest } from "../../type/models";
import { capitalizeFirstLetter } from "../../type/utils";
import MapModal from "../../component/objects/MapModal";
import AddObjectModal from "../../component/map/AddObjectModal";

const ObjectsPage = () => {
  const { onOpen, onClose, isOpen } = useDisclosure();
  const [keyword, setKeyword] = useState<string>("");
  const [debouncedKeyword, setDebouncedKeyword] = useState<string>("");
  const [selectedCamera, setSelectedCamera] = useState<string | null>(null);
  const [selectedType, setSelectedType] = useState<InfraType | null>(null);
  
  const [filterCriteria, setFilterCriteria] = useState<ObjectFilterRequest>({
    name: null,
    category: null,
    dateCaptured: null,
    location: null,
    status: null,
    cameraId: selectedCamera,
    keyword: debouncedKeyword,
    type: null,
    keyId: null,
    page: 0,
    size: 10,
  });

  const { data: objects, isLoading } = useGetObjectsQuery({
    requestBody: filterCriteria,
  });

  const { data: cameras } = useGetCamerasQuery();

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedKeyword(keyword);
    }, 500);

    return () => clearTimeout(handler);
  }, [keyword]);

  useEffect(() => {
    setFilterCriteria(prev => ({
      ...prev,
      page: 0,
      keyword: debouncedKeyword,
    }));
  }, [debouncedKeyword]);

  useEffect(() => {
    setFilterCriteria(prev => ({
      ...prev,
      cameraId: selectedCamera,
      type: selectedType,
      page: 0,
    }));
  }, [selectedCamera, selectedType]);

  const handlePageChange = (page: number) => {
    setFilterCriteria((prev) => ({ ...prev, page }));
  };

  const handleClearFilters = () => {
    setKeyword("");
    setSelectedCamera(null);
    setSelectedType(InfraType.ASSET);
    setFilterCriteria(prev => ({
      ...prev,
      name: null,
      category: null,
      dateCaptured: null,
      location: null,
      status: null,
      cameraId: null,
      keyword: "",
      type: InfraType.ASSET,
      page: 0,
      size: 10,
    }));
  };

  // Map InfraType to corresponding React Feather icons
  const typeIcons = {
    [InfraType.ASSET]: BoxIcon,
    [InfraType.ABNORMALITY]: AlertTriangle,
  };

  return (
    <VStack 
      spacing={6} 
      align="stretch" 
      px={2} 
      py={2} 
      height="full" 
      bg="gray.50"
      borderRadius="lg"
    >
      <Flex 
        justify="space-between" 
        align="center" 
        width="full" 
      >
        <HStack spacing={4}>
          {/* Camera Selection with Icon */}
          <HStack>
            <CameraIcon size="20px" color="gray.600" />
            <Select
              placeholder="All cameras"
              value={selectedCamera || ""}
              onChange={(e) => setSelectedCamera(e.target.value || null)}
              variant="filled"
              bg="gray.100"
              borderRadius="md"
              minWidth="200px"
            >
              {cameras?.map((camera) => (
                <option key={camera.id} value={camera.id}>
                  {camera.name}
                </option>
              ))}
            </Select>
          </HStack>

          {/* Type Selection with Dynamic Icons */}
          <HStack>
            <Layers size="20px" color="gray.600" />
            <Select
              placeholder="All types"
              value={selectedType || ""}
              onChange={(e) => setSelectedType(e.target.value as InfraType)}
              variant="filled"
              bg="gray.100"
              borderRadius="md"
              minWidth="200px"
            >
              {Object.values(InfraType).map((type) => {
                const TypeIcon = typeIcons[type];
                return (
                  <option key={type} value={type}>
                    {capitalizeFirstLetter(type)}
                  </option>
                );
              })}
            </Select>
          </HStack>
        </HStack>

        {/* Search Bar */}
        <HStack width="50%">
          <Search size="20px" color="gray.500" />
          <Input
            placeholder="Search objects..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            variant="filled"
            bg="gray.100"
            borderRadius="md"
          />
          <Button
            colorScheme="blue"
            variant={"solid"}
            onClick={onOpen}
          >
            Add Object
          </Button>
          <Button
            leftIcon={<X size={16} />}
            colorScheme="gray"
            variant="outline"
            onClick={handleClearFilters}
          >
            Clear
          </Button>
        </HStack>
      </Flex>

      <AddObjectModal
        isOpen={isOpen}
        onClose={onClose}
      />

      {/* Table and Pagination */}
      {isLoading ? (
        <Flex justify="center" align="center" height="300px">
          <Spinner size="xl" color="blue.500" />
        </Flex>
      ) : (
        <>
          {objects && (
            <ObjectTable
              objects={objects.pageData}
              filterCriteria={filterCriteria}
              onFilterChange={setFilterCriteria}
              onClear={handleClearFilters}
            />
          )}
          
          {objects && (
            <Flex justify="center" >
              <Pagination
                currentPage={objects.pageNumber}
                totalPages={objects.totalPages}
                onPageChange={handlePageChange}
              />
            </Flex>
          )}
        </>
      )}
    </VStack>
  );
};

export default ObjectsPage;