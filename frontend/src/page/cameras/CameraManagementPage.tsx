import React, { useState } from 'react';
import { useGetCamerasQuery } from '../../redux/service/camera';
import { Camera } from '../../type/models';
import { Box, Button, Container, Flex, Heading, Spinner } from '@chakra-ui/react';
import { Plus } from 'react-feather';
import CameraList from '../../component/cameras/CameraList';
import VideoStream from '../../component/cameras/VideoStream';
import AddCameraForm from '../../component/cameras/AddCameraForm';

const CameraManagementPage: React.FC = () => {
  const { data: cameras, isLoading, error } = useGetCamerasQuery();
  const [selectedCamera, setSelectedCamera] = useState<Camera | undefined>();
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  if (isLoading) return (
    <Flex height="100vh" align="center" justify="center">
      <Spinner size="xl" />
    </Flex>
  );
  
  if (error) return (
    <Flex height="100vh" align="center" justify="center">
      Error loading cameras
    </Flex>
  );

  return (
    <Container
      maxW="full"
      height="100%"
      p={0}
      overflow="hidden"
    >
      <Flex height="100%" overflow="hidden">
        {/* Camera List */}
        <CameraList
          cameras={cameras || []}
          onSelectCamera={setSelectedCamera}
          selectedCamera={selectedCamera}
        />

        {/* Video Stream and Details */}
        <Flex
          flex="1"
          direction="column"
          overflow="hidden"
        >
          {/* Top Bar */}
          <Flex
            justify="space-between"
            align="center"
            p={4}
            borderBottom="1px solid"
            borderColor="gray.200"
            flexShrink={0}
          >
            <Heading size="lg">Camera Management</Heading>
            <Button
              onClick={() => setIsAddModalOpen(true)}
              colorScheme="green"
              leftIcon={<Plus />}
            >
              Add Camera
            </Button>
          </Flex>

          <VideoStream camera={selectedCamera} />
        </Flex>
      </Flex>
      <AddCameraForm
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
      />
    </Container>
  );
};

export default CameraManagementPage;