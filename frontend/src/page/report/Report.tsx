import {
  Button,
  VStack,
  Text,
  Box,
  Tabs,
  TabList,
  Tab,
  TabPanels,
  TabPanel,
  Icon,
  Divider,
  Flex,
  FormControl,
  FormLabel,
  Select,
  Input,
  HStack,
  Heading,
  Container,
  Card,
  CardHeader,
  CardBody,
  useColorModeValue,
  Alert,
  AlertIcon,
  AlertTitle,
  AlertDescription,
  InputGroup,
  InputLeftElement,
  Tooltip,
  Badge,
  SimpleGrid,
} from "@chakra-ui/react";
import {
  FileText,
  Download,
  Calendar,
  Camera,
  RefreshCw,
  Eye,
} from "react-feather";
import { useState } from "react";
import { useForm, Controller } from "react-hook-form";
import { read, utils } from "xlsx";
import useCustomToast from "../../hook/useCustomToast";
import { appConfig } from "../../config";
import { useGetCamerasQuery } from "../../redux/service/camera";
import { ReportGenerateRequest } from "../../type/models";
import { formatDate, validateDates } from "../../type/utils";

interface Sheet {
  name: string;
  content: string;
}

const Report = () => {
  const token = localStorage.getItem("access_token");
  const showToast = useCustomToast();
  const { data: cameras, isLoading: isCamerasLoading } = useGetCamerasQuery();
  const [sheets, setSheets] = useState<Sheet[]>([]);
  const [downloadUrl, setDownloadUrl] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isError, setIsError] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string>("");

  // Theme colors
  const cardBg = useColorModeValue("white", "gray.800");
  const headerBg = useColorModeValue("gray.50", "gray.700");
  const tabBg = useColorModeValue("white", "gray.700");
  const previewBg = useColorModeValue("gray.50", "gray.700");
  const borderColor = useColorModeValue("gray.200", "gray.600");

  const {
    register,
    handleSubmit,
    watch,
    control,
    reset,
    formState: { errors, isValid },
  } = useForm<ReportGenerateRequest>({
    mode: "onChange",
  });

  const selectedCamera = watch("cameraId");
  const startDate = watch("startTime");
  const endDate = watch("endTime");

  const handleGenerateReport = async (data: ReportGenerateRequest) => {
    if (validateDates(startDate, endDate) !== true) {
      showToast("Error", validateDates(startDate, endDate) as string, "error");
      return;
    }

    try {
      setIsLoading(true);
      setIsError(false);
      setErrorMessage("");

      const response = await fetch(`${appConfig.baseUrl}/reports/camera`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || "Failed to generate report");
      }

      const blob = await response.blob();
      const url = URL.createObjectURL(blob);
      setDownloadUrl(url);

      const ab = await blob.arrayBuffer();
      const wb = read(ab);
      const newSheets = wb.SheetNames.map((name) => ({
        name,
        content: utils.sheet_to_html(wb.Sheets[name]),
      }));

      setSheets(newSheets);
      showToast("Success", "Report generated successfully", "success");
    } catch (error) {
      setIsError(true);
      setErrorMessage(
        error instanceof Error ? error.message : "Failed to generate report"
      );
      showToast(
        "Error",
        error instanceof Error ? error.message : "Failed to generate report",
        "error"
      );
    } finally {
      setIsLoading(false);
    }
  };

  const resetForm = () => {
    reset();
    setSheets([]);
    setDownloadUrl(null);
    setIsError(false);
    setErrorMessage("");
  };

  const getSelectedCameraName = () => {
    if (!selectedCamera || !cameras) return null;
    const camera = cameras.find((c) => c.id === selectedCamera);
    return camera?.name || null;
  };

  return (
    <Container maxW="100%" py={4}>
      <VStack spacing={6} align="stretch" w="full">
        <Card variant="outline" shadow="sm" borderRadius="xl">
          <CardBody>
            <form onSubmit={handleSubmit(handleGenerateReport)}>
              <Flex gap={4} align="end" wrap="nowrap">
                {/* Camera */}
                <FormControl isInvalid={!!errors.cameraId} w="25%" minW="150px">
                  <FormLabel fontWeight="semibold" mb={1}>
                    <Flex align="center" gap={1}>
                      <Icon as={Camera} boxSize={4} />
                      Camera
                    </Flex>
                  </FormLabel>
                  <Controller
                    name="cameraId"
                    control={control}
                    rules={{ required: "Camera is required" }}
                    render={({ field }) => (
                      <Select
                        placeholder="Select camera"
                        isDisabled={isCamerasLoading || isLoading}
                        size="sm"
                        {...field}
                      >
                        {cameras?.map((camera) => (
                          <option key={camera.id} value={camera.id}>
                            {camera.name}
                          </option>
                        ))}
                      </Select>
                    )}
                  />
                </FormControl>

                {/* Start Date */}
                <FormControl
                  isInvalid={!!errors.startTime}
                  w="25%"
                  minW="150px"
                >
                  <FormLabel fontWeight="semibold" mb={1}>
                    <Flex align="center" gap={1}>
                      <Icon as={Calendar} boxSize={4} />
                      Start
                    </Flex>
                  </FormLabel>
                  <Input
                    type="date"
                    size="sm"
                    {...register("startTime", {
                      required: "Start date is required",
                    })}
                    isDisabled={isLoading}
                  />
                </FormControl>

                {/* End Date */}
                <FormControl isInvalid={!!errors.endTime} w="25%" minW="150px">
                  <FormLabel fontWeight="semibold" mb={1}>
                    <Flex align="center" gap={1}>
                      <Icon as={Calendar} boxSize={4} />
                      End
                    </Flex>
                  </FormLabel>
                  <Input
                    type="date"
                    size="sm"
                    {...register("endTime", {
                      required: "End date is required",
                    })}
                    isDisabled={isLoading}
                  />
                </FormControl>

                {/* Button */}
                <Box w="25%" minW="150px" display="flex" alignItems="flex-end">
                  <Button
                    type="submit"
                    leftIcon={<Icon as={Eye} boxSize={4} />}
                    colorScheme="blue"
                    isLoading={isLoading}
                    loadingText="Generating..."
                    isDisabled={!isValid || isCamerasLoading}
                    size="sm"
                    px={5}
                    borderRadius="md"
                    w="100%"
                  >
                    Generate
                  </Button>
                </Box>
              </Flex>
            </form>
          </CardBody>
        </Card>

        {/* Error feedback */}
        {isError && (
          <Alert status="error" variant="left-accent" borderRadius="md">
            <AlertIcon />
            <Box>
              <AlertTitle>Error generating report</AlertTitle>
              <AlertDescription>
                {errorMessage ||
                  "Unable to generate the report. Please try again."}
              </AlertDescription>
            </Box>
          </Alert>
        )}

        {/* Export Button */}
        {downloadUrl && (
          <Card
            variant="outline"
            shadow="sm"
            borderColor="green.200"
            borderRadius="md"
            bg="green.50"
          >
            <CardBody py={4}>
              <Flex align="center" justify="space-between">
                <VStack align="start" spacing={1}>
                  <Badge colorScheme="green" fontSize="sm">
                    Ready to export
                  </Badge>
                  <Text fontWeight="medium">
                    Report for {getSelectedCameraName()} from{" "}
                    {formatDate(startDate || "")} to {formatDate(endDate || "")}
                  </Text>
                </VStack>
                <Button
                  leftIcon={<Icon as={Download} boxSize={5} />}
                  colorScheme="green"
                  as="a"
                  href={downloadUrl}
                  download="report.xlsx"
                  size="md"
                >
                  Export Excel
                </Button>
              </Flex>
            </CardBody>
          </Card>
        )}

        {/* Report Preview */}
        {sheets.length > 0 && (
          <Card
            variant="outline"
            shadow="md"
            borderRadius="lg"
            overflow="hidden"
          >
            <CardHeader bg={headerBg} py={4}>
              <Flex align="center" justify="space-between">
                <Heading size="md">Report Preview</Heading>
                <Text fontSize="sm" color="gray.500">
                  {sheets.length} {sheets.length === 1 ? "sheet" : "sheets"}
                </Text>
              </Flex>
            </CardHeader>
            <CardBody p={0}>
              <Tabs variant="enclosed" colorScheme="blue" size="md">
                <TabList
                  bg={headerBg}
                  px={2}
                  pt={2}
                  overflowX="auto"
                  flexWrap="nowrap"
                >
                  {sheets.map((sheet, index) => (
                    <Tab
                      key={index}
                      fontWeight="medium"
                      px={4}
                      py={2}
                      _selected={{
                        bg: tabBg,
                        borderColor: borderColor,
                        borderBottomColor: "transparent",
                        fontWeight: "bold",
                      }}
                    >
                      {sheet.name}
                    </Tab>
                  ))}
                </TabList>

                <TabPanels>
                  {sheets.map((sheet, index) => (
                    <TabPanel key={index} p={0}>
                      <Box overflow="auto" maxH="500px" p={4} bg={previewBg}>
                        <Box bg="white" p={4} borderRadius="md" shadow="sm">
                          <div
                            dangerouslySetInnerHTML={{ __html: sheet.content }}
                          ></div>
                        </Box>
                      </Box>
                    </TabPanel>
                  ))}
                </TabPanels>
              </Tabs>
            </CardBody>
          </Card>
        )}

        {/* Empty state */}
        {!isLoading && sheets.length === 0 && !isError && (
          <Flex
            justify="center"
            align="center"
            direction="column"
            py={10}
            px={4}
            borderWidth="1px"
            borderRadius="lg"
            borderStyle="dashed"
            borderColor={borderColor}
            bg={previewBg}
          >
            <Icon as={FileText} boxSize={12} color="gray.400" mb={4} />
            <Text fontSize="lg" fontWeight="medium" mb={1}>
              No report generated yet
            </Text>
            <Text fontSize="md" color="gray.500" textAlign="center" maxW="md">
              Select a camera and date range, then click "Generate Preview" to
              see your report here.
            </Text>
          </Flex>
        )}
      </VStack>
    </Container>
  );
};

export default Report;
