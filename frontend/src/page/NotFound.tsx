import { Container, Text, Button, Flex, Icon } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { AlertCircle, ArrowLeft, Home } from "react-feather";

const NotFound = () => {
  const navigate = useNavigate();

  return (
    <Flex
      w="full"
      h="100vh"
      alignItems="center"
      justifyContent="center"
      textAlign="center"
    >
      <Container>
        <Text
          fontSize={"8xl"}
          fontWeight={"bold"}
          lineHeight={"1"}
          mb={4}
        >
          404
        </Text>

        <Icon as={AlertCircle} w={12} h={12} mb={4} color="red.500" />
        <Text fontSize={"md"}>Oops!</Text>
        <Text fontSize={"md"}>Page not found</Text>

        <Flex align={"center"} justify={"center"} direction={"column"}>
          <Button
            onClick={() => navigate(-1)}
            variant={"solid"}
            mt={4}
            leftIcon={<ArrowLeft size={20} />}
          >
            Go back
          </Button>

          <Button
            onClick={() => navigate("/")}
            variant={"solid"}
            mt={4}
            leftIcon={<Home size={20} />}
          >
            Return to Home
          </Button>

        </Flex>

      </Container>
    </Flex>
  );
};

export default NotFound;