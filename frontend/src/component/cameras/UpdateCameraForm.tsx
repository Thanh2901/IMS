import { Box, VStack, FormControl, FormLabel, Input, Button } from "@chakra-ui/react";
import { useState } from "react";
import { Edit2, Plus } from "react-feather";
import { Camera, CreateCameraRequest, UpdateCameraRequest } from "../../type/models";
import useCustomToast from "../../hook/useCustomToast";

interface CameraFormProps {
  camera?: Camera;
  onSubmit: (data: CreateCameraRequest | UpdateCameraRequest) => void;
  isEditing?: boolean;
}

const UpdateCameraForm: React.FC<CameraFormProps> = ({ camera, onSubmit, isEditing = false }) => {
  const showToast = useCustomToast();
  const [formData, setFormData] = useState({
    name: camera?.name || "",
    rtsp: camera?.rtsp || "",
    ipAddress: camera?.ipAddress || "",
    port: camera?.port || "",
    username: "",
    password: "",
    cameraUserList: camera?.cameraUserList?.map(cu => ({ userId: cu.userId })) || []
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <Box as="form" onSubmit={handleSubmit}>
      <VStack spacing={4}>
        <FormControl isRequired>
          <FormLabel>Camera Name</FormLabel>
          <Input 
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="Enter camera name"
          />
        </FormControl>

        <FormControl isRequired>
          <FormLabel>IP Address</FormLabel>
          <Input 
            name="ipAddress"
            value={formData.ipAddress}
            onChange={handleChange}
            placeholder="Enter IP address"
          />
        </FormControl>

        <FormControl isRequired>
          <FormLabel>RTSP Stream URL</FormLabel>
          <Input 
            name="rtsp"
            value={formData.rtsp}
            onChange={handleChange}
            placeholder="Enter RTSP stream URL"
          />
        </FormControl>

        <FormControl isRequired>
          <FormLabel>Imaging Port</FormLabel>
          <Input 
            name="imagingPort"
            type="number"
            value={formData.imagingPort}
            onChange={handleChange}
            placeholder="Enter imaging port"
          />
        </FormControl>

        {!isEditing && (
          <>
            <FormControl isRequired>
              <FormLabel>Username</FormLabel>
              <Input 
                name="username"
                value={formData.username}
                onChange={handleChange}
                placeholder="Enter camera username"
              />
            </FormControl>

            <FormControl isRequired>
              <FormLabel>Password</FormLabel>
              <Input 
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Enter camera password"
              />
            </FormControl>
          </>
        )}

        <Button 
          type="submit" 
          colorScheme="blue" 
          leftIcon={isEditing ? <Edit2 /> : <Plus />}
        >
          {isEditing ? 'Update Camera' : 'Add Camera'}
        </Button>
      </VStack>
    </Box>
  );
};

export default UpdateCameraForm;