import { useEffect } from 'react';
import {
  Box,
  Button,
  Divider,
  FormControl,
  FormErrorMessage,
  FormLabel,
  Heading,
  Input,
  VStack,
  Text,
  Link,
  Spinner,
} from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import useAuth from "../../hook/useAuth";
import { LoginRequest } from '../../type/models';

interface LoginForm {
  email: string;
  password: string;
}

interface LoginProps {
  fcmToken: string | null;
};

const Login: React.FC<LoginProps> = ({ fcmToken }) => {
  const { isLoggedIn, loginFunc } = useAuth();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting }
  } = useForm<LoginForm>();

  const onSubmit = async (data: LoginForm) => {
    const request: LoginRequest = { email: data.email, password: data.password, fcmToken };
    console.log(request);
    await loginFunc(request);
  };

  useEffect(() => {
    if (isLoggedIn()) {
      navigate("/dashboard");
    }
  }, [navigate]);

  return (
    <Box
      minH="100vh"
      display="flex"
      alignItems="center"
      justifyContent="center"
      px={4}
    >
      <Box
        w={["full", "md"]}
        bg="white"
        shadow="xl"
        rounded="lg"
        p={8}
        borderWidth={1}
        borderColor="gray.200"
      >
        <Heading as="h2" size="lg" textAlign="center" color="blue.500" mb={6}>
          Welcome Back
        </Heading>

        <form onSubmit={handleSubmit(onSubmit)}>
          <VStack spacing={5}>
            <FormControl isInvalid={!!errors.email}>
              <FormLabel htmlFor="email" fontSize="sm" color="gray.700">
                Email Address
              </FormLabel>
              <Input
                id="email"
                type="email"
                placeholder="Enter your email"
                {...register("email", {
                  required: "Email is required",
                  pattern: {
                    value: /^\S+@\S+$/i,
                    message: "Invalid email address"
                  }
                })}
                focusBorderColor="blue.500"
                borderColor="gray.300"
                color="gray.800"
                _placeholder={{ color: "gray.500" }}
                _hover={{ borderColor: "gray.400" }}
              />
              <FormErrorMessage>{errors.email?.message}</FormErrorMessage>
            </FormControl>

            <FormControl isInvalid={!!errors.password}>
              <FormLabel htmlFor="password" fontSize="sm" color="gray.700">
                Password
              </FormLabel>
              <Input
                id="password"
                type="password"
                placeholder="Enter your password"
                {...register("password", {
                  required: "Password is required",
                  minLength: {
                    value: 6,
                    message: "Password must be at least 6 characters"
                  }
                })}
                focusBorderColor="blue.500"
                borderColor="gray.300"
                color="gray.800"
                _placeholder={{ color: "gray.500" }}
                _hover={{ borderColor: "gray.400" }}
              />
              <FormErrorMessage>{errors.password?.message}</FormErrorMessage>
            </FormControl>

            <Box
              display="flex"
              width="full"
              flexDirection="column"
              alignItems="flex-end"
            >
              <Link
                color="blue.500"
                fontSize="sm"
                onClick={() => navigate('/forget-password')}
                _hover={{ textDecoration: "underline" }}
                mb={4}
              >
                Forget Password?
              </Link>

              <Button
                type="submit"
                isLoading={isSubmitting}
                colorScheme="blue"
                width="full"
                size="lg"
                py={6}
                _hover={{ bg: "blue.600" }}
              >
                {isSubmitting ? <Spinner /> : 'Log In'}
              </Button>
            </Box>
          </VStack>
        </form>

        <VStack my={6} spacing={2} alignItems="center">
          <Divider borderColor="gray.300" />
          <Text fontSize="sm" color="gray.500">or</Text>
          <Divider borderColor="gray.300" />
        </VStack>

        <Button
          colorScheme="blue"
          variant="outline"
          width="full"
          size="lg"
          onClick={() => navigate('/register')}
          py={6}
          _hover={{ bg: "blue.50" }}
        >
          Create New Account
        </Button>
      </Box>
    </Box>
  );
};

export default Login;