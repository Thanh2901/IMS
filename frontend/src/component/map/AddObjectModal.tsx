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
  useDisclosure,
} from "@chakra-ui/react";
import { AddInfrastructureObjectRequest, InfraCategory, InfraStatus } from "../../type/models";
import { useAddInfrastructureObjectMutation } from "../../redux/service/infrastructure";
import { useGetCamerasQuery } from "../../redux/service/camera";
import useCustomToast from "../../hook/useCustomToast";
import MapModal from "../objects/MapModal";

interface AddObjectModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSelect?: (objectId: string | null) => void;
  latitude?: number;
  longitude?: number;
}

// Form Fields Component
const FormFields: React.FC<{
  data: Partial<AddInfrastructureObjectRequest>;
  errors: Record<string, string>;
  cameras: any[] | undefined;
  isCamerasLoading: boolean;
  originalImage: File | null;
  avatar: File | null;
  handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => void;
  handleChangeOriginalImage: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleChangeAvatar: (e: React.ChangeEvent<HTMLInputElement>) => void;
}> = ({ data, errors, cameras, isCamerasLoading, originalImage, avatar, handleChange, handleChangeOriginalImage, handleChangeAvatar }) => {
  const [previewOriginalImage, setPreviewOriginalImage] = useState<string | null>(null);
  const [previewAvatar, setPreviewAvatar] = useState<string | null>(null);

  useEffect(() => {
    if (originalImage) {
      const url = URL.createObjectURL(originalImage);
      setPreviewOriginalImage(url);
      return () => URL.revokeObjectURL(url);
    } else {
      setPreviewOriginalImage(null);
    }
  }, [originalImage]);

  useEffect(() => {
    if (avatar) {
      const url = URL.createObjectURL(avatar);
      setPreviewAvatar(url);
      return () => URL.revokeObjectURL(url);
    } else {
      setPreviewAvatar(null);
    }
  }, [avatar]);


  return (
    <HStack>
      <VStack spacing={1} align="center">
        <FormControl isInvalid={!!errors.cameraId} isRequired>
          <FormLabel>Camera</FormLabel>
          <Select name="cameraId" onChange={handleChange} value={data.cameraId} isDisabled={isCamerasLoading}>
            {cameras?.map((camera) => (
              <option key={camera.id} value={camera.id}>{camera.name}</option>
            ))}
          </Select>
          <FormErrorMessage>{errors.cameraId}</FormErrorMessage>
        </FormControl>

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

        <FormControl isInvalid={!!errors.longitude} isRequired>
          <FormLabel>Longitude</FormLabel>
          <Input name="longitude" value={data.longitude} onChange={handleChange}/>
          <FormErrorMessage>{errors.longitude}</FormErrorMessage>
        </FormControl>

        <FormControl isInvalid={!!errors.latitude} isRequired>
          <FormLabel>Latitude</FormLabel>
          <Input name="latitude" value={data.latitude} onChange={handleChange}/>
          <FormErrorMessage>{errors.latitude}</FormErrorMessage>
        </FormControl>
      </VStack>

      <VStack spacing={1} align={"center"}>
        <FormControl>
          <FormLabel>Additional Info (optional)</FormLabel>
          <Input name="additional" value={data.additional} onChange={handleChange} />
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
          <Input name="manageUnit" value={data.manageUnit} onChange={handleChange} />
        </FormControl>
      </VStack>

    </HStack>
  );
};

// Main Modal Component
const AddObjectModal: React.FC<AddObjectModalProps> = ({ isOpen, onClose, latitude, longitude, onSelect }) => {
  const showToast = useCustomToast();
  const { isOpen: isMapOpen, onOpen: onOpenMap, onClose: onCloseMap } = useDisclosure();
  const { data: cameras, isLoading: isCamerasLoading } = useGetCamerasQuery();
  const [addInfraObject, { isLoading }] = useAddInfrastructureObjectMutation();
  const [originalImage, setOriginalImage] = useState<File | null>(null);
  const [avatar, setAvatar] = useState<File | null>(null);
  const [data, setData] = useState<Partial<AddInfrastructureObjectRequest>>({
    cameraId: "",
    longitude,
    latitude,
    category: InfraCategory.SIGN,
    name: "",
    status: InfraStatus.OK,
    additional: "",
    manageUnit: ""
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (cameras?.length) {
      setData((prev) => ({ ...prev, cameraId: cameras[0].id }));
    }
  }, [cameras]);

  const validate = () => {
    let newErrors: Record<string, string> = {};
    if (!data.cameraId) newErrors.cameraId = "Camera selection is required";
    if (!data.name?.trim()) newErrors.name = "Object name is required";
    if (!data.category) newErrors.category = "Category is required";
    if (!data.status) newErrors.status = "Status is required";
    if (!data.longitude) newErrors.longitude = "Longitude is required";
    if (!data.latitude) newErrors.latitude = "Latitude is required";
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setData((prev) => ({ ...prev, [name]: value }));
  };

  const handleChangeOriginalImage = (e: React.ChangeEvent<HTMLInputElement>) => {
    setOriginalImage(e.target.files?.[0] || null);
  };

  const handleChangeAvatar = (e: React.ChangeEvent<HTMLInputElement>) => {
    setAvatar(e.target.files?.[0] || null);
  };

  const handleSetLatitude = (lat: number) => {
    setData((prev) => ({ ...prev, latitude: lat }));
  };

  const handleSetLongitude = (lon: number) => {
    setData((prev) => ({ ...prev, longitude: lon }));
  };

  const handleAddObject = async () => {
    if (!validate()) {
      showToast("Error", "Please fill all required fields!", "error");
      return;
    }
    try {
      const formData = new FormData();
      formData.append("data", JSON.stringify(data));
      if (originalImage) {
        formData.append("originalImage", originalImage);
      }
      if (avatar) {
        formData.append("avatar", avatar);
      }
      const response = await addInfraObject({ formData }).unwrap();
      showToast("Success", "Infrastructure object added successfully!", "success");

      if (onSelect) {
        onSelect(response.id);
      }

      onClose();

    } catch (error: any) {
      showToast("Error", error.message || "Failed to add object", "error");
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} isCentered size="xl">
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>Add Infrastructure Object</ModalHeader>
        <ModalCloseButton />

        <ModalBody>
          <FormFields
            data={data}
            errors={errors}
            cameras={cameras}
            isCamerasLoading={isCamerasLoading}
            originalImage={originalImage}
            avatar={avatar}
            handleChange={handleChange}
            handleChangeOriginalImage={handleChangeOriginalImage}
            handleChangeAvatar={handleChangeAvatar}
          />
          {/* Add a button to open the map */}
          <Button onClick={onOpenMap} mt={4} colorScheme="teal" variant="outline">
            Select Location on Map
          </Button>
        </ModalBody>

        <ModalFooter>
          <Button colorScheme="blue" onClick={handleAddObject} isLoading={isLoading || isCamerasLoading}>
            Add Object
          </Button>
          <Button variant="ghost" onClick={onClose} ml={3}>
            Cancel
          </Button>
        </ModalFooter>

        <MapModal 
          isOpen={isMapOpen} 
          onClose={onCloseMap} 
          setLatitude={handleSetLatitude} 
          setLongitude={handleSetLongitude}
        />

      </ModalContent>
    </Modal>
  );
};

export default AddObjectModal;