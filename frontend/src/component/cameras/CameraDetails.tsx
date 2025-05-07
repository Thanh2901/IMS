import React from 'react';
import { 
  Card, 
  CardHeader, 
  CardBody, 
  Flex, 
  Heading, 
  VStack, 
  Text, 
  Grid, 
  GridItem,
  Box,
  Divider
} from '@chakra-ui/react';
import { 
  Server, 
  Clock, 
  Camera as CameraIcon, 
  Link as LinkIcon 
} from 'react-feather';
import { Camera } from '../../type/models';
import { formatDate } from '../../type/utils';

const DetailRow: React.FC<{ 
  icon: React.ElementType, 
  label: string, 
  value: string | number 
}> = ({ icon: Icon, label, value }) => (
  <Flex 
    alignItems="center" 
    bg="gray.50" 
    p={3} 
    borderRadius="md" 
    gap={3}
  >
    <Icon size={16} />
    <Box>
      <Text fontSize="xs" color="gray.500" fontWeight="medium">
        {label}
      </Text>
      <Text color="gray.800" fontWeight="semibold">
        {value}
      </Text>
    </Box>
  </Flex>
);

const CameraDetails: React.FC<{ camera: Camera }> = ({ camera }) => {
  return (
    <Card 
      w="full" 
      maxW="md" 
      boxShadow="lg" 
      borderRadius="xl" 
      overflow="hidden"
    >
      <CardHeader 
        bg="gray.100" 
        borderBottomWidth={1} 
        borderBottomColor="gray.200"
      >
        <Flex alignItems="center">
          <CameraIcon color="#2C5282" size={24} />
          <Heading 
            ml={3} 
            size="md" 
            color="gray.800"
          >
            {camera.name}
          </Heading>
        </Flex>
      </CardHeader>
      
      <CardBody>
        <VStack spacing={4} align="stretch">
          <Grid templateColumns="1fr" gap={3}>
            <GridItem>
              <DetailRow 
                icon={Server}
                label="IP Address"
                value={camera.ipAddress}
              />
            </GridItem>
            <GridItem>
              <DetailRow 
                icon={LinkIcon}
                label="RTSP Stream"
                value={camera.rtsp}
              />
            </GridItem>
            <GridItem>
              <DetailRow 
                icon={Clock}
                label="Imaging Port"
                value={camera.imagingPort}
              />
            </GridItem>
          </Grid>

          <Divider my={2} borderColor="gray.200" />

          <VStack 
            spacing={2} 
            align="stretch" 
            fontSize="sm" 
            color="gray.600"
          >
            <Flex justifyContent="space-between">
              <Text fontWeight="medium">Created At:</Text>
              <Text>{formatDate(camera.createdAt)}</Text>
            </Flex>
            <Flex justifyContent="space-between">
              <Text fontWeight="medium">Updated At:</Text>
              <Text>{formatDate(camera.updatedAt)}</Text>
            </Flex>
            <Flex justifyContent="space-between">
              <Text fontWeight="medium">Associated Users:</Text>
              <Text>{camera.cameraUserList.length}</Text>
            </Flex>
          </VStack>
        </VStack>
      </CardBody>
    </Card>
  );
};

export default CameraDetails;