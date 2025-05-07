import React, { useEffect, useState } from "react";
import {
  VStack,
  Flex,
  Input,
  Select,
  Button,
  Spinner,
  Box,
  HStack,
  InputGroup,
  InputLeftElement,
  useColorModeValue,
  useToast
} from "@chakra-ui/react";
import { Camera, Search, X, Download } from "react-feather";
import { useGetEventsQuery } from "../../redux/service/infrastructure";
import { useGetCamerasQuery } from "../../redux/service/camera";
import EventTable from "../../component/events/EventTable";
import Pagination from "../../component/common/Pagination";
import { EventFilterRequest, PageResponse } from "../../type/models";
import * as XLSX from 'xlsx';
import { transformApiResponse } from "../../redux/service";

const Events: React.FC = () => {
  const [keyword, setKeyword] = useState<string>("");
  const [debouncedKeyword, setDebouncedKeyword] = useState<string>("");
  const [selectedCamera, setSelectedCamera] = useState<string | null>(null);
  const [isExporting, setIsExporting] = useState<boolean>(false);
  const toast = useToast();
  
  const [filterCriteria, setFilterCriteria] = useState<EventFilterRequest>({
    startTime: null,
    endTime: null,
    eventStatus: null,
    status: null,
    category: null,
    cameraId: selectedCamera,
    location: null,
    confidence: null,
    name: null,
    level: null,
    keyword: debouncedKeyword,
    page: 0,
    size: 10,
  });

  // Background colors for light and dark modes
  const bgColor = useColorModeValue("white", "gray.800");
  const borderColor = useColorModeValue("gray.300", "gray.600");

  // Queries
  const { data: events, isLoading } = useGetEventsQuery({
    requestBody: filterCriteria,
  });

  const { data: cameras } = useGetCamerasQuery();

  // Debounce keyword input
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedKeyword(keyword);
    }, 500);

    return () => clearTimeout(handler);
  }, [keyword]);

  // Update filter criteria when keyword changes
  useEffect(() => {
    setFilterCriteria(prev => ({
      ...prev,
      page: 0,
      keyword: debouncedKeyword,
    }));
  }, [debouncedKeyword]);

  // Update filter criteria when camera changes
  useEffect(() => {
    setFilterCriteria(prev => ({
      ...prev,
      cameraId: selectedCamera,
      page: 0,
    }));
  }, [selectedCamera]);

  // Page change handler
  const handlePageChange = (page: number) => {
    setFilterCriteria((prev) => ({ ...prev, page }));
  };

  // Clear all filters
  const handleClearFilters = () => {
    setKeyword("");
    setSelectedCamera(null);
    setFilterCriteria({
      startTime: null,
      endTime: null,
      eventStatus: null,
      status: null,
      category: null,
      cameraId: null,
      location: null,
      confidence: null,
      name: null,
      level: null,
      keyword: "",
      page: 0,
      size: 10,
    });
  };

  // Export to Excel function
  const exportToExcel = async () => {
    try {
      setIsExporting(true);
      
      // Create export filter criteria with larger size
      const exportFilterCriteria: EventFilterRequest = {
        ...filterCriteria,
        page: 0,
        size: 10000 // Using a large size to get all filtered data
      };

      const response = await fetch("/infrastructures/events/filter", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          // Thêm token nếu cần:
          // Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(exportFilterCriteria)
      });
  
      const rawData = await response.json();
      const events = transformApiResponse<PageResponse<Event>>(rawData);
      
      // Format the data for Excel
      const formattedData = events.pageData.map((event: any) => ({
        ID: event.id,
        Name: event.name,
        Camera: event.cameraName,
        Status: event.status,
        Category: event.category,
        Location: event.location,
        Confidence: event.confidence,
        'Created At': new Date(event.createdAt).toLocaleString(),
        Level: event.level
      }));
      
      // Create a workbook and worksheet
      const worksheet = XLSX.utils.json_to_sheet(formattedData);
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, 'Events');
      
      // Generate the Excel file
      const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
      const data = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      
      // Create download link
      const fileName = `events_export_${new Date().toISOString().split('T')[0]}.xlsx`;
      const url = window.URL.createObjectURL(data);
      const link = document.createElement('a');
      link.href = url;
      link.download = fileName;
      link.click();
      
      // Clean up
      window.URL.revokeObjectURL(url);
      
      toast({
        title: "Export successful",
        description: `${formattedData.length} events exported to Excel`,
        status: "success",
        duration: 5000,
        isClosable: true,
      });
    } catch (error) {
      console.error("Export error:", error);
      toast({
        title: "Export failed",
        description: "An error occurred while exporting data",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setIsExporting(false);
    }
  };

  return (
    <VStack 
      spacing={6} 
      align="stretch" 
      px={1} 
      py={1} 
      height="full"
    >
      <Flex 
        justify="space-between" 
        align="center" 
        width="full"
        flexDirection={{ base: "column", md: "row" }}
        gap={4}
      >
        {/* Camera Selection */}
        <HStack width={{ base: "full", md: "auto" }}>
          <Select
            placeholder="All cameras"
            value={selectedCamera || ""}
            onChange={(e) => setSelectedCamera(e.target.value || null)}
            bg={bgColor}
            borderRadius="lg"
            boxShadow="sm"
            borderColor={borderColor}
            _focus={{ 
              borderColor: "blue.500", 
              boxShadow: "md" 
            }}
            width={{ base: "full", md: "250px" }}
          >
            {cameras?.map((camera) => (
              <option key={camera.id} value={camera.id}>
                {camera.name}
              </option>
            ))}
          </Select>
        </HStack>

        {/* Search, Export, and Clear Filters */}
        <Flex 
          width={{ base: "full", md: "auto" }} 
          alignItems="center"
        >
          <InputGroup flex={1}>
            <InputLeftElement pointerEvents="none">
              <Search size="20" />
            </InputLeftElement>
            <Input
              placeholder="Search an event..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              bg={bgColor}
              borderRadius="lg"
              boxShadow="sm"
              borderWidth="1px"
              borderColor={borderColor}
              _focus={{ 
                borderColor: "blue.500", 
                boxShadow: "md" 
              }}
              paddingLeft="40px"
            />
          </InputGroup>
          
          <Button
            ml={4}
            leftIcon={<Download size={16} />}
            colorScheme="green"
            onClick={exportToExcel}
            isLoading={isExporting}
            loadingText="Exporting"
          >
            Export
          </Button>
          
          <Button
            ml={4}
            leftIcon={<X size={16} />}
            variant="outline"
            colorScheme="gray"
            onClick={handleClearFilters}
          >
            Clear
          </Button>
        </Flex>
      </Flex>

      {/* Event Table */}
      {events && (
        <EventTable
          events={events.pageData}
          filterCriteria={filterCriteria}
          onFilterChange={setFilterCriteria}
          onClear={handleClearFilters}
        />
      )}

      {/* Loading Spinner */}
      {isLoading && (
        <Flex justify="center" align="center" py={4}>
          <Spinner size="lg" color="blue.500" />
        </Flex>
      )}

      {/* Pagination */}
      {events && (
        <Flex justify="center" >
          <Pagination
            currentPage={events.pageNumber}
            totalPages={events.totalPages}
            onPageChange={handlePageChange}
          />
        </Flex>
      )}
    </VStack>
  );
};

export default Events;