import React, { useEffect, useState } from "react";
import {
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalFooter,
  ModalBody,
  ModalCloseButton,
  Button,
  VStack,
  FormControl,
  FormLabel,
  FormErrorMessage,
  Input,
  Select,
  Image,
  Box,
  HStack,
} from "@chakra-ui/react";
import { InfraCategory, InfraStatus, UpdateObjectRequest } from "../../type/models";
import { useUpdateInfrastructureObjectMutation, useGetObjectByIdQuery } from "../../redux/service/infrastructure";
import { useGetCamerasQuery } from "../../redux/service/camera";
import useCustomToast from "../../hook/useCustomToast";

interface EditObjectModalProps {
  isOpen: boolean;
  onClose: () => void;
  objectId: string | null;
}

// Form Fields Component
const FormFields: React.FC<{
  data: any;
  errors: Record<string, string>;
  cameras: any[] | undefined;
  isCamerasLoading: boolean;
  originalImage: File | null;
  avatar: File | null;
  originalImageUrl: string | null;
  avatarUrl: string | null;
  handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => void;
  handleChangeOriginalImage: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleChangeAvatar: (e: React.ChangeEvent<HTMLInputElement>) => void;
}> = ({ 
  data, 
  errors, 
  originalImage, 
  avatar, 
  originalImageUrl, 
  avatarUrl, 
  handleChange, 
  handleChangeOriginalImage, 
  handleChangeAvatar 
}) => {
  const [previewOriginalImage, setPreviewOriginalImage] = useState<string | null>(originalImageUrl);
  const [previewAvatar, setPreviewAvatar] = useState<string | null>(avatarUrl);

  useEffect(() => {
    if (originalImage) {
      const url = URL.createObjectURL(originalImage);
      setPreviewOriginalImage(url);
      return () => URL.revokeObjectURL(url);
    } else if (originalImageUrl) {
      setPreviewOriginalImage(originalImageUrl);
    } else {
      setPreviewOriginalImage(null);
    }
  }, [originalImage, originalImageUrl]);

  useEffect(() => {
    if (avatar) {
      const url = URL.createObjectURL(avatar);
      setPreviewAvatar(url);
      return () => URL.revokeObjectURL(url);
    } else if (avatarUrl) {
      setPreviewAvatar(avatarUrl);
    } else {
      setPreviewAvatar(null);
    }
  }, [avatar, avatarUrl]);

  return (
    <HStack>
      <VStack spacing={1} align="center">
        <FormControl isInvalid={!!errors.name} isRequired>
          <FormLabel>Object Name</FormLabel>
          <Input name="name" value={data.name} onChange={handleChange} />
          <FormErrorMessage>{errors.name}</FormErrorMessage>
        </FormControl>

        <FormControl isInvalid={!!errors.category} isRequired>
          <FormLabel>Category</FormLabel>
          <Select name="category" value={data.category} onChange={handleChange}>
            {Object.values(InfraCategory).map((cat) => (
              <option key={cat} value={cat}>{cat}</option>
            ))}
          </Select>
          <FormErrorMessage>{errors.category}</FormErrorMessage>
        </FormControl>

        <FormControl isInvalid={!!errors.status} isRequired>
          <FormLabel>Status</FormLabel>
          <Select name="status" value={data.status} onChange={handleChange}>
            {Object.values(InfraStatus).map((status) => (
              <option key={status} value={status}>{status}</option>
            ))}
          </Select>
          <FormErrorMessage>{errors.status}</FormErrorMessage>
        </FormControl>

        <FormControl isRequired>
          <FormLabel>Longitude</FormLabel>
          <Input value={data.longitude} isReadOnly />
        </FormControl>

        <FormControl isRequired>
          <FormLabel>Latitude</FormLabel>
          <Input value={data.latitude} isReadOnly />
        </FormControl>
      </VStack>

      <VStack spacing={1} align={"center"}>
        <FormControl>
          <FormLabel>Additional Info (optional)</FormLabel>
          <Input name="additional" value={data.additional || ""} onChange={handleChange} />
        </FormControl>

        <FormControl>
          <FormLabel>Upload Object Image (optional)</FormLabel>
          <Input type="file" onChange={handleChangeOriginalImage} accept="image/*" />
          {previewOriginalImage && (
            <Box mt={2}>
              <Image src={previewOriginalImage} maxH="200px" objectFit="contain" alt="Preview" />
            </Box>
          )}
        </FormControl>

        <FormControl>
          <FormLabel>Avatar (optional)</FormLabel>
          <Input type="file" onChange={handleChangeAvatar} accept="image/*" />
          {previewAvatar && (
            <Box mt={2}>
              <Image src={previewAvatar} maxH="200px" objectFit="contain" alt="Preview" />
            </Box>
          )}
        </FormControl>

        <FormControl>
          <FormLabel>Manage Unit</FormLabel>
          <Input name="manageUnit" value={data.manageUnit || ""} onChange={handleChange} />
        </FormControl>
      </VStack>
    </HStack>
  );
};

// Main Modal Component
const EditObjectModal: React.FC<EditObjectModalProps> = ({ isOpen, onClose, objectId }) => {
  const showToast = useCustomToast();
  const { data: cameras, isLoading: isCamerasLoading } = useGetCamerasQuery();
  const { data: objectData, isLoading: isObjectLoading } = useGetObjectByIdQuery(
    objectId || "", 
    { skip: !objectId }
  );
  const [updateInfraObject, { isLoading: isUpdating }] = useUpdateInfrastructureObjectMutation();
  
  const [originalImage, setOriginalImage] = useState<File | null>(null);
  const [avatar, setAvatar] = useState<File | null>(null);
  const [formData, setFormData] = useState<UpdateObjectRequest>({
    infraId: "",
    longitude: 0,
    latitude: 0,
    category: InfraCategory.SIGN,
    name: "",
    status: InfraStatus.OK,
    additional: "",
    manageUnit: ""
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  // Load object data when objectId changes or when object data is fetched
  useEffect(() => {
    if (objectData) {
      setFormData({
        infraId: objectData.id,
        longitude: objectData.longitude,
        latitude: objectData.latitude,
        category: objectData.category,
        name: objectData.name,
        status: objectData.status,
        additional: "",
        manageUnit: objectData.manageUnit || ""
      });
    }
  }, [objectData]);

  const validate = () => {
    let newErrors: Record<string, string> = {};
    if (!formData.name.trim()) newErrors.name = "Object name is required";
    if (!formData.category) newErrors.category = "Category is required";
    if (!formData.status) newErrors.status = "Status is required";
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleChangeOriginalImage = (e: React.ChangeEvent<HTMLInputElement>) => {
    setOriginalImage(e.target.files?.[0] || null);
  };

  const handleChangeAvatar = (e: React.ChangeEvent<HTMLInputElement>) => {
    setAvatar(e.target.files?.[0] || null);
  };

  const handleUpdateObject = async () => {
    if (!validate()) {
      showToast("Error", "Please fill all required fields!", "error");
      return;
    }
    
    try {
      const updateFormData = new FormData();
      updateFormData.append("data", JSON.stringify(formData));
      
      if (originalImage) {
        updateFormData.append("originalImage", originalImage);
      }
      
      if (avatar) {
        updateFormData.append("avatar", avatar);
      }
      
      await updateInfraObject({formData: updateFormData}).unwrap();
      showToast("Success", "Infrastructure object updated successfully!", "success");
      onClose();
    } catch (error: any) {
      showToast("Error", error.message || "Failed to update object", "error");
    }
  };

  // Show loading state if object data or cameras are still loading
  if (isObjectLoading && objectId) {
    return (
      <Modal isOpen={isOpen} onClose={onClose} isCentered size="xl">
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Edit Infrastructure Object</ModalHeader>
          <ModalCloseButton />
          <ModalBody>Loading object data...</ModalBody>
        </ModalContent>
      </Modal>
    );
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose} isCentered size="xl">
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>Edit Infrastructure Object</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <FormFields
            data={formData}
            errors={errors}
            cameras={cameras}
            isCamerasLoading={isCamerasLoading}
            originalImage={originalImage}
            avatar={avatar}
            originalImageUrl={objectData?.info?.originalImage || null}
            avatarUrl={objectData?.info?.avatar || null}
            handleChange={handleChange}
            handleChangeOriginalImage={handleChangeOriginalImage}
            handleChangeAvatar={handleChangeAvatar}
          />
        </ModalBody>
        <ModalFooter>
          <Button colorScheme="blue" onClick={handleUpdateObject} isLoading={isUpdating || isCamerasLoading}>
            Update Object
          </Button>
          <Button variant="ghost" onClick={onClose} ml={3}>
            Cancel
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default EditObjectModal;