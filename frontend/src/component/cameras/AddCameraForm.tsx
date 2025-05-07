import { Box, VStack, FormControl, FormLabel, Input, Button, Modal, ModalOverlay, ModalContent, ModalHeader, ModalBody, FormErrorMessage, ModalCloseButton } from "@chakra-ui/react";
import { useForm } from "react-hook-form";
import { Plus } from "react-feather";
import { Camera, CreateCameraRequest } from "../../type/models";
import useCustomToast from "../../hook/useCustomToast";
import { useCreateCameraMutation } from "../../redux/service/camera";

interface AddCameraFormProps {
  isOpen: boolean;
  onClose: () => void;
};

const AddCameraForm: React.FC<AddCameraFormProps> = ({ isOpen, onClose }) => {
  const showToast = useCustomToast();
  const { register, handleSubmit, reset, formState: { errors } } = useForm<CreateCameraRequest>();
  const [createCamera, { isLoading }] = useCreateCameraMutation();

  const onSubmit = async (data: CreateCameraRequest) => {
    try {
      await createCamera({ requestBody: data }).unwrap();
      showToast("Success", "Add camera successfully", "success");
      reset();
      onClose();
    } catch (error: any) {
      showToast("Error", "Failed to add new camera", "error");
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>Add Camera</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <Box as="form" onSubmit={handleSubmit(onSubmit)}>
            <VStack spacing={4}>
              <FormControl isInvalid={!!errors.name} isRequired>
                <FormLabel>Camera Name</FormLabel>
                <Input {...register("name", { required: "Camera name is required" })} placeholder="Enter camera name" />
                <FormErrorMessage>{errors.name?.message}</FormErrorMessage>
              </FormControl>

              <FormControl isInvalid={!!errors.ipAddress} isRequired>
                <FormLabel>IP Address</FormLabel>
                <Input {...register("ipAddress", { required: "IP Address is required" })} placeholder="Enter IP address" />
                <FormErrorMessage>{errors.ipAddress?.message}</FormErrorMessage>
              </FormControl>

              {/* <FormControl isInvalid={!!errors.rtsp} isRequired>
                <FormLabel>RTSP Stream URL</FormLabel>
                <Input {...register("rtsp", { required: "RTSP URL is required" })} placeholder="Enter RTSP stream URL" />
                <FormErrorMessage>{errors.rtsp?.message}</FormErrorMessage>
              </FormControl> */}

              <FormControl isInvalid={!!errors.port} isRequired>
                <FormLabel>Port</FormLabel>
                <Input type="number" {...register("port", { required: "Port is required" })} placeholder="Enter camera port" />
                <FormErrorMessage>{errors.port?.message}</FormErrorMessage>
              </FormControl>

              <FormControl isInvalid={!!errors.username} isRequired>
                <FormLabel>Username</FormLabel>
                <Input {...register("username", { required: "Username is required" })} placeholder="Enter camera username" />
                <FormErrorMessage>{errors.username?.message}</FormErrorMessage>
              </FormControl>

              <FormControl isInvalid={!!errors.password} isRequired>
                <FormLabel>Password</FormLabel>
                <Input type="password" {...register("password", { required: "Password is required" })} placeholder="Enter camera password" />
                <FormErrorMessage>{errors.password?.message}</FormErrorMessage>
              </FormControl>

              <Button type="submit" colorScheme="blue" leftIcon={<Plus />} isLoading={isLoading}>
                Add Camera
              </Button>
            </VStack>
          </Box>
        </ModalBody>
      </ModalContent>
    </Modal>
  );
};

export default AddCameraForm;