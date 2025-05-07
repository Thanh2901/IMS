import {
  Box,
  Container,
  Heading,
  Text,
  VStack,
  Button,
  SimpleGrid,
  Icon,
  useColorModeValue,
} from "@chakra-ui/react";
import { useEffect } from "react";
import { Map, BarChart, Bell, Database } from "react-feather";
import { useNavigate } from "react-router-dom";
import useAuth from "../hook/useAuth";

const Home = () => {
  const { isLoggedIn } = useAuth();
  const bgColor = useColorModeValue("gray.50", "gray.800");
  const featureIconColor = useColorModeValue("blue.500", "blue.200");
  const navigate = useNavigate();

  useEffect(() => {
    if (isLoggedIn()) {
      navigate("/dashboard");
    }
  }, [navigate]);

  return (
    <Box width={"full"}>
      {/* Hero Section */}
      <Box bg={bgColor} py={20}>
        <Container maxW={"container.lg"} textAlign="center">
          <Heading as="h1" size="2xl" mb={4}>
            Infrastructure Monitoring System
          </Heading>
          <Text fontSize="xl" color="gray.600" mb={8}>
            Monitor the status of road infrastructure objects such as guardrails, road surfaces, lamp posts, and traffic signs accurately and efficiently.
          </Text>
          <Button colorScheme="blue" size="lg" onClick={() => navigate("/login")}>
            Get Started
          </Button>
        </Container>
      </Box>

      {/* Features Section */}
      <Box py={20}>
        <Container maxW={"container.lg"}>
          <Heading as="h2" size="xl" textAlign="center" mb={10}>
            Key Features
          </Heading>
          <SimpleGrid columns={{ base: 1, md: 2, lg: 4 }} spacing={10}>
            {/* Feature 1: Object Recognition */}
            <VStack spacing={4} textAlign="center">
              <Icon as={Map} w={10} h={10} color={featureIconColor} />
              <Heading as="h3" size="md">
                Object Recognition
              </Heading>
              <Text color="gray.600">
                Use AI to detect and classify infrastructure objects such as guardrails, road surfaces, trees, lamp posts, and traffic signs.
              </Text>
            </VStack>

            {/* Feature 2: Status Monitoring */}
            <VStack spacing={4} textAlign="center">
              <Icon as={BarChart} w={10} h={10} color={featureIconColor} />
              <Heading as="h3" size="md">
                Status Monitoring
              </Heading>
              <Text color="gray.600">
                Track the real-time status of detected objects and display their location on a map interface.
              </Text>
            </VStack>

            {/* Feature 3: Alerts and Notifications */}
            <VStack spacing={4} textAlign="center">
              <Icon as={Bell} w={10} h={10} color={featureIconColor} />
              <Heading as="h3" size="md">
                Alerts and Notifications
              </Heading>
              <Text color="gray.600">
                Receive instant alerts for any abnormalities detected in the infrastructure.
              </Text>
            </VStack>

            {/* Feature 4: Data Storage and Analysis */}
            <VStack spacing={4} textAlign="center">
              <Icon as={Database} w={10} h={10} color={featureIconColor} />
              <Heading as="h3" size="md">
                Data Storage and Analysis
              </Heading>
              <Text color="gray.600">
                Store and analyze long-term data to support infrastructure management and decision-making.
              </Text>
            </VStack>
          </SimpleGrid>
        </Container>
      </Box>
    </Box>
  );
};

export default Home;