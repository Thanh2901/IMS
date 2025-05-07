import {
  Box,
  Button,
  FormControl,
  FormErrorMessage,
  FormLabel,
  Heading,
  Input,
  VStack,
  Link,
  Spinner,
} from "@chakra-ui/react";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import useAuth from "../../hook/useAuth";
import { RegisterRequest } from "../../type/models";

interface RegisterForm {
  email: string;
  fullName: string;
  phone: string | null;
  address: string | null;
  password: string;
  confirmPassword: string;
}

const Register = () => {
  const { isLoggedIn, registerFunc } = useAuth();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<RegisterForm>({
    mode: "onChange",
  });

  const onSubmit = async (data: RegisterForm) => {
    const request: RegisterRequest = {
      email: data.email,
      fullName: data.fullName,
      phone: data.phone,
      address: data.address,
      password: data.password,
    };
    await registerFunc(request);
  };

  useEffect(() => {
    if (isLoggedIn()) {
      navigate("/dashboard");
    }
  }, [isLoggedIn, navigate]);

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
          Create New Account
        </Heading>

        <form onSubmit={handleSubmit(onSubmit)}>
          <VStack spacing={5}>
            <FormControl isInvalid={!!errors.fullName}>
              <FormLabel htmlFor="fullName" fontSize="sm" color="gray.700">
                Full Name
              </FormLabel>
              <Input
                id="fullName"
                type="text"
                placeholder="Enter your full name"
                {...register("fullName", {
                  required: "Name is required",
                })}
                focusBorderColor="blue.500"
                borderColor="gray.300"
                color="gray.800"
                _placeholder={{ color: "gray.500" }}
                _hover={{ borderColor: "gray.400" }}
              />
              <FormErrorMessage>{errors.fullName?.message}</FormErrorMessage>
            </FormControl>

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
                    message: "Invalid email address",
                  },
                })}
                focusBorderColor="blue.500"
                borderColor="gray.300"
                color="gray.800"
                _placeholder={{ color: "gray.500" }}
                _hover={{ borderColor: "gray.400" }}
              />
              <FormErrorMessage>{errors.email?.message}</FormErrorMessage>
            </FormControl>

            <FormControl isInvalid={!!errors.phone}>
              <FormLabel htmlFor="phone" fontSize="sm" color="gray.700">
                Phone Number (Optional)
              </FormLabel>
              <Input
                id="phone"
                type="tel"
                placeholder="Enter your phone number"
                {...register("phone")}
                focusBorderColor="blue.500"
                borderColor="gray.300"
                color="gray.800"
                _placeholder={{ color: "gray.500" }}
                _hover={{ borderColor: "gray.400" }}
              />
              <FormErrorMessage>{errors.phone?.message}</FormErrorMessage>
            </FormControl>

            <FormControl isInvalid={!!errors.address}>
              <FormLabel htmlFor="address" fontSize="sm" color="gray.700">
                Address (Optional)
              </FormLabel>
              <Input
                id="address"
                type="text"
                placeholder="Enter your address"
                {...register("address")}
                focusBorderColor="blue.500"
                borderColor="gray.300"
                color="gray.800"
                _placeholder={{ color: "gray.500" }}
                _hover={{ borderColor: "gray.400" }}
              />
              <FormErrorMessage>{errors.address?.message}</FormErrorMessage>
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
                    message: "Password must be at least 6 characters",
                  },
                })}
                focusBorderColor="blue.500"
                borderColor="gray.300"
                color="gray.800"
                _placeholder={{ color: "gray.500" }}
                _hover={{ borderColor: "gray.400" }}
              />
              <FormErrorMessage>{errors.password?.message}</FormErrorMessage>
            </FormControl>

            <FormControl isInvalid={!!errors.confirmPassword}>
              <FormLabel
                htmlFor="confirmPassword"
                fontSize="sm"
                color="gray.700"
              >
                Confirm Password
              </FormLabel>
              <Input
                id="confirmPassword"
                type="password"
                placeholder="Confirm your password"
                {...register("confirmPassword", {
                  required: "Please confirm your password",
                  validate: (value) =>
                    value === watch("password") || "Passwords do not match",
                })}
                focusBorderColor="blue.500"
                borderColor="gray.300"
                color="gray.800"
                _placeholder={{ color: "gray.500" }}
                _hover={{ borderColor: "gray.400" }}
              />
              <FormErrorMessage>
                {errors.confirmPassword?.message}
              </FormErrorMessage>
            </FormControl>

            <Box
              width="full"
              display="flex"
              flexDirection="column"
              alignItems="flex-end"
            >
              <Link
                fontSize="sm"
                onClick={() => navigate("/login")}
                color="blue.500"
                mb={4}
                _hover={{ textDecoration: "underline" }}
              >
                Already have an account? Log in
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
                {isSubmitting ? <Spinner /> : "Sign Up"}
              </Button>
            </Box>
          </VStack>
        </form>
      </Box>
    </Box>
  );
};

export default Register;