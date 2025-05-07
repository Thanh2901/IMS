import React, { useEffect, useState } from "react";
import {
  Box,
  Grid,
  Heading,
  Input,
  Text,
  Stat,
  StatLabel,
  StatNumber,
  Flex,
  Skeleton,
  useBreakpointValue,
  Select,
} from "@chakra-ui/react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  BarChart,
  Bar,
} from "recharts";
import { useGetCamerasQuery } from "../../redux/service/camera";
import { useGetStatisticsQuery } from "../../redux/service/infrastructure";

const Dashboard = () => {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [cameraId, setCameraId] = useState("");
  const { data: cameras, isLoading: isCameraLoading } = useGetCamerasQuery();

  const { data: statistic, isLoading, refetch } = useGetStatisticsQuery({
    params: {
      cameraId: cameraId,
      startDate: startDate,
      endDate: endDate
    },
  });

  useEffect(() => {
    if (!cameraId && cameras && cameras.length > 0) {
      setCameraId(cameras[0].id);
    }
  }, [cameraId, cameras]);


  const eventData = statistic?.eventStatistics ? statistic.eventStatistics : [];

  const objectData = statistic?.objectStatistics ? statistic.objectStatistics : [];

  const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042"];

  const statusData = [
    { name: "OK", count: objectData.filter((obj) => obj.status === "OK").reduce((sum, obj) => sum + obj.count, 0) },
    { name: "NOT OK", count: objectData.filter((obj) => obj.status === "NOT OK").reduce((sum, obj) => sum + obj.count, 0) }
  ];

  const categoryData = Array.from(
    objectData.reduce((map, obj) => {
      map.set(obj.category, (map.get(obj.category) || 0) + obj.count);
      return map;
    }, new Map())
  ).map(([name, count]) => ({ name, count }));

  const groupedData = Array.from(
    objectData.reduce((map, obj) => {
      const category = obj.category;
      if (!map.has(category)) {
        map.set(category, { category, OK: 0, NOT_OK: 0 });
      }
      map.get(category)[obj.status === "OK" ? "OK" : "NOT_OK"] += obj.count;
      return map;
    }, new Map()).values()
  );

  const gridColumn = useBreakpointValue({ base: "1fr", md: "repeat(2, 1fr)", lg: "repeat(3, 1fr)" });

  return (
    <Box
      minW="7xl"
    >
      <Box mb={4}>
        <Grid templateColumns={{ base: "1fr", md: "repeat(3, 1fr)" }} gap={4}>
          <Box>
            <Text fontWeight="medium" mb={2}>
              Camera
            </Text>
            {isCameraLoading ? (
              <Skeleton height="40px" width="100%" />
            ) : (
              <Select
                value={cameraId}
                onChange={(e) => setCameraId(e.target.value)}
              >
                {cameras?.map((camera) => (
                  <option key={camera.id} value={camera.id}>
                    {camera.name}
                  </option>
                ))}
              </Select>
            )}
          </Box>
          <Box>
            <Text fontWeight="medium" mb={2}>
              Start Date
            </Text>
            <Input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
            />
          </Box>
          <Box>
            <Text fontWeight="medium" mb={2}>
              End Date
            </Text>
            <Input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
            />
          </Box>
        </Grid>
      </Box>

      <Grid templateColumns={gridColumn} gap={8}>
        <Box bg="white" p={4} rounded="lg" shadow="md">
          <Heading as="h2" size="md" mb={4}>
            Status Distribution
          </Heading>
          <Box h="64">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={statusData}
                  cx="50%"
                  cy="50%"
                  label={({ name, value }) => `${name}: ${value}`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="count"
                >
                  {statusData.map((entry, index) => (
                    <Cell
                      key={`cell-${index}`}
                      fill={entry.name === "NOT OK" ? "#FF0000" : COLORS[index % COLORS.length]}
                    />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </Box>
        </Box>

        <Box bg="white" p={4} rounded="lg" shadow="md">
          <Heading as="h2" size="md" mb={4}>
            Category Distribution
          </Heading>
          <Box h="64">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={categoryData}
                  cx="50%"
                  cy="50%"
                  label={({ name, value }) => `${name}: ${value}`}
                  outerRadius={80}
                  fill="#82ca9d"
                  dataKey="count"
                >
                  {categoryData.map((entry, index) => (
                    <Cell
                      key={`cell-${index}`}
                      fill={COLORS[index % COLORS.length]}
                    />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </Box>
        </Box>

        <Box bg="white" p={4} rounded="lg" shadow="md">
          <Heading as="h2" size="md" mb={4}>
            Category Status Distribution
          </Heading>
          <Box h="64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={groupedData} barGap={4}>
                <XAxis dataKey="category" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="OK" fill="#00C49F" name="OK" />
                <Bar dataKey="NOT_OK" fill="#FF0000" name="NOT OK" />
              </BarChart>
            </ResponsiveContainer>
          </Box>
        </Box>

        <Box bg="white" p={4} rounded="lg" shadow="md" gridColumn={{ base: "1fr", md: "span 2" }}>
          <Heading as="h2" size="md" mb={4}>
            Event Timeline
          </Heading>
          <Box h="64">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={eventData}>
                <XAxis dataKey="dateCaptured" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line
                  type="monotone"
                  dataKey="count"
                  stroke="#8884d8"
                  strokeWidth={2}
                  dot={{ r: 4 }}
                  activeDot={{ r: 8 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </Box>
        </Box>

        <Box bg="white" p={6} rounded="lg" shadow="md" gridColumn={{ base: "1fr", md: "span 2", lg: "span 1" }}>
          <Heading as="h2" size="md" mb={4}>
            Summary Statistics
          </Heading>
          <Grid templateColumns={{ base: "1fr", md: "repeat(2, 1fr)" }} gap={4}>
            <Stat bg="blue.50" p={4} rounded="lg">
              <StatLabel>Total Events</StatLabel>
              <StatNumber>
                {eventData.reduce((sum, event) => sum + event.count, 0)}
              </StatNumber>
            </Stat>
            <Stat bg="green.50" p={4} rounded="lg">
              <StatLabel>Total Objects</StatLabel>
              <StatNumber>
                {objectData.reduce((sum, obj) => sum + obj.count, 0)}
              </StatNumber>
            </Stat>
            <Stat bg="yellow.50" p={4} rounded="lg">
              <StatLabel>OK Status</StatLabel>
              <StatNumber>
                {objectData
                  .filter((obj) => obj.status === "OK")
                  .reduce((sum, obj) => sum + obj.count, 0)}
              </StatNumber>
            </Stat>
            <Stat bg="red.50" p={4} rounded="lg">
              <StatLabel>NOT OK Status</StatLabel>
              <StatNumber>
                {objectData
                  .filter((obj) => obj.status === "NOT OK")
                  .reduce((sum, obj) => sum + obj.count, 0)}
              </StatNumber>
            </Stat>
          </Grid>
        </Box>
      </Grid>
    </Box>
  );
};

export default Dashboard;
