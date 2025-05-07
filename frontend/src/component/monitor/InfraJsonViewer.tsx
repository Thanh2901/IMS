import React, { useEffect, useState } from "react";
import {
  Box,
  VStack,
  Text,
  Spinner,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  SimpleGrid,
  Stat,
  StatLabel,
  StatNumber,
  Icon,
  Button,
  HStack,
  useDisclosure,
  Tooltip,
} from "@chakra-ui/react";
import { useParams, useNavigate } from "react-router-dom";
import {
  FaCheckCircle,
  FaPlusCircle,
  FaTimesCircle,
  FaRuler,
  FaArrowLeft,
  FaFileCsv,
  FaFileDownload,
  FaSearch,
} from "react-icons/fa";
import { saveAs } from "file-saver";
import { InfraObject } from "../../type/models";
import InfraComparePopup from "./InfraComparePopup";

export interface InfraRecord {
  oldInfraObject: InfraObject | null;
  newInfraObject: InfraObject | null;
  distance: number | null;
}

const InfraJsonViewer = () => {
  const [data, setData] = useState<InfraRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [stats, setStats] = useState({
    updatedCount: 0,
    updatedDistanceAvg: 0,
    newObjectsCount: 0,
    notUpdatedCount: 0,
  });

  const [newInfraStats, setNewInfraStats] = useState({
    count: 0,
    avgConfidence: 0,
    okRatio: 0,
    notOkRatio: 0,
    categoryRatio: {} as { [key: string]: number },
  });

  const [selectedCompareData, setSelectedCompareData] =
    useState<InfraRecord | null>(null);
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { scheduleId } = useParams<{ scheduleId: string }>();
  const navigate = useNavigate();

  useEffect(() => {
    if (scheduleId) {
      const fetchData = async () => {
        setLoading(true);
        setError(null);
        try {
          const response = await fetch(
            `http://localhost:9000/infra-logs/${scheduleId}.json`
          );
          if (!response.ok) throw new Error("JSON file not found");
          const json = await response.json();
          if (Array.isArray(json)) {
            setData(json);
          } else {
            setError("Invalid JSON format (not an array)");
          }
        } catch (err: any) {
          setError(err.message || "An error occurred");
        } finally {
          setLoading(false);
        }
      };

      fetchData();
    }
  }, [scheduleId]);

  useEffect(() => {
    const newObjectsOnly = data.filter((d) => d.newInfraObject);
    if (newObjectsOnly.length > 0) {
      const count = newObjectsOnly.length;
      const totalConfidence = newObjectsOnly.reduce(
        (sum, item) => sum + (item.newInfraObject?.confidence || 0),
        0
      );
      const avgConfidence = totalConfidence / count;

      const okCount = newObjectsOnly.filter(
        (item) =>
          item.newInfraObject?.confidence &&
          item.newInfraObject.confidence >= 0.8
      ).length;
      const notOkCount = count - okCount;

      const categoryCount: { [key: string]: number } = {};
      newObjectsOnly.forEach((item) => {
        const cat = item.newInfraObject?.category || "Unknown";
        categoryCount[cat] = (categoryCount[cat] || 0) + 1;
      });

      const categoryRatio = Object.fromEntries(
        Object.entries(categoryCount).map(([cat, catCount]) => [
          cat,
          (catCount / count) * 100,
        ])
      );

      setNewInfraStats({
        count,
        avgConfidence,
        okRatio: (okCount / count) * 100,
        notOkRatio: (notOkCount / count) * 100,
        categoryRatio,
      });
    }
  }, [data]);

  useEffect(() => {
    if (data.length > 0) {
      const updatedItems = data.filter(
        (item) => item.oldInfraObject && item.newInfraObject
      );
      const totalDistance = updatedItems.reduce(
        (sum, item) => sum + (item.distance || 0),
        0
      );
      const updatedDistanceAvg =
        updatedItems.length > 0 ? totalDistance / updatedItems.length : 0;

      const newObjectsCount = data.filter(
        (item) => !item.oldInfraObject && item.newInfraObject
      ).length;
      const notUpdatedCount = data.filter(
        (item) => item.oldInfraObject && !item.newInfraObject
      ).length;

      setStats({
        updatedCount: updatedItems.length,
        updatedDistanceAvg,
        newObjectsCount,
        notUpdatedCount,
      });
    }
  }, [data]);

  const handleShowObjectOnMap = (objectId: string | undefined) => {
    if (!objectId) {
      alert("ID not found to show on map.");
      return;
    }
    navigate("/map", { state: { clickedObjectId: objectId } });
  };

  const downloadJson = () => {
    const blob = new Blob([JSON.stringify(data, null, 2)], {
      type: "application/json",
    });
    saveAs(blob, `infra-log-${scheduleId}.json`);
  };

  const renderTable = (items: InfraRecord[], showDistance: boolean = false) => (
    <Box overflowX="auto">
      <Table variant="striped" size="sm" layout="fixed" w="100%">
        <Thead>
          <Tr>
            <Th w="15%">Name</Th>
            <Th w="15%">Category</Th>
            <Th w="20%">Actions</Th>
            {showDistance ? <Th w="10%">Distance (m)</Th> : <Th w="10%"></Th>}
            <Th w="35%">Location</Th>
          </Tr>
        </Thead>
        <Tbody>
          {items.map((item, idx) => {
            const obj = item.oldInfraObject || item.newInfraObject;
            return (
              <Tr key={idx}>
                <Td w="20%">{obj?.name || "N/A"}</Td>
                <Td w="15%">{obj?.category || "N/A"}</Td>
                <Td w="20%">
                  <HStack spacing={2}>
                    <Button
                      size="sm"
                      colorScheme="blue"
                      onClick={() => handleShowObjectOnMap(obj?.id)}
                    >
                      View Map
                    </Button>
                    {item.oldInfraObject && item.newInfraObject && (
                      <Button
                        size="sm"
                        colorScheme="purple"
                        leftIcon={<FaSearch />}
                        onClick={() => {
                          setSelectedCompareData(item);
                          onOpen();
                        }}
                      >
                        Compare
                      </Button>
                    )}
                  </HStack>
                </Td>
                {showDistance ? (
                  <Td w="10%">
                    {item.distance !== null ? item.distance.toFixed(2) : "N/A"}
                  </Td>
                ) : (
                  <Td w="10%"></Td>
                )}

                <Td w="35%">
                  <Tooltip
                    label={obj?.location || "N/A"}
                    hasArrow
                    placement="top"
                  >
                    <span
                      style={{
                        whiteSpace: "nowrap",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        display: "block",
                      }}
                    >
                      {obj?.location || "N/A"}
                    </span>
                  </Tooltip>
                </Td>
              </Tr>
            );
          })}
        </Tbody>
      </Table>
    </Box>
  );

  const newObjects = data.filter((d) => !d.oldInfraObject && d.newInfraObject);
  const notUpdated = data.filter((d) => d.oldInfraObject && !d.newInfraObject);
  const updated = data.filter((d) => d.oldInfraObject && d.newInfraObject);

  return (
    <VStack p={4} spacing={6} w="full" align="stretch">
      <HStack>
        <Button
          leftIcon={<FaArrowLeft />}
          colorScheme="gray"
          onClick={() => navigate("/monitor")}
        >
          Back
        </Button>
        <Button
          leftIcon={<FaFileDownload />}
          colorScheme="orange"
          onClick={downloadJson}
        >
          Export JSON
        </Button>
      </HStack>

      {loading && <Spinner size="lg" />}
      {error && <Text color="red.500">{error}</Text>}

      {!loading && data.length > 0 && (
        <>
          <SimpleGrid columns={{ base: 1, md: 7 }} spacing={4} w="full">
            <Stat p={4} shadow="md" rounded="xl" bg="red.100">
              <StatLabel display="flex" alignItems="center" gap={2}>
                <Icon as={FaTimesCircle} color="red.500" /> Not Updated
              </StatLabel>
              <StatNumber>{stats.notUpdatedCount}</StatNumber>
            </Stat>

            <Stat p={4} shadow="md" rounded="xl" bg="green.100">
              <StatLabel display="flex" alignItems="center" gap={2}>
                <Icon as={FaCheckCircle} color="green.500" /> Updated
              </StatLabel>
              <StatNumber>{stats.updatedCount}</StatNumber>
            </Stat>

            <Stat p={4} shadow="md" rounded="xl" bg="blue.100">
              <StatLabel display="flex" alignItems="center" gap={2}>
                <Icon as={FaPlusCircle} color="blue.500" /> New
              </StatLabel>
              <StatNumber>{stats.newObjectsCount}</StatNumber>
            </Stat>

            <Stat p={4} shadow="md" rounded="xl" bg="purple.100">
              <StatLabel display="flex" alignItems="center" gap={2}>
                <Icon as={FaRuler} color="purple.500" /> Avg. Distance
              </StatLabel>
              <StatNumber>{stats.updatedDistanceAvg.toFixed(2)} m</StatNumber>
            </Stat>
            <Stat p={4} shadow="md" rounded="xl" bg="teal.100">
              <StatLabel>Avg. Confidence</StatLabel>
              <StatNumber>{newInfraStats.avgConfidence.toFixed(2)}</StatNumber>
            </Stat>
            <Stat p={4} shadow="md" rounded="xl" bg="green.100">
              <StatLabel>OK (%)</StatLabel>
              <StatNumber>{newInfraStats.okRatio.toFixed(1)}%</StatNumber>
            </Stat>
            <Stat p={4} shadow="md" rounded="xl" bg="red.100">
              <StatLabel>Not OK (%)</StatLabel>
              <StatNumber>{newInfraStats.notOkRatio.toFixed(1)}%</StatNumber>
            </Stat>
          </SimpleGrid>

          <Tabs variant="enclosed" colorScheme="teal" mt={6}>
            <TabList>
              <Tab>New ({newObjects.length})</Tab>
              <Tab>Not Updated ({notUpdated.length})</Tab>
              <Tab>Updated ({updated.length})</Tab>
            </TabList>
            <TabPanels>
              <TabPanel>{renderTable(newObjects)}</TabPanel>
              <TabPanel>{renderTable(notUpdated)}</TabPanel>
              <TabPanel>{renderTable(updated, true)}</TabPanel>
            </TabPanels>
          </Tabs>
        </>
      )}

      {selectedCompareData && (
        <InfraComparePopup
          infraRecord={selectedCompareData}
          isOpen={isOpen}
          onClose={onClose}
        />
      )}
    </VStack>
  );
};

export default InfraJsonViewer;
