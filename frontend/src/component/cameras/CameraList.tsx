import React from 'react';
import { Box, Text, Accordion, AccordionItem, AccordionButton, Flex, AccordionIcon, AccordionPanel, VStack, Badge, Divider } from "@chakra-ui/react";
import { Server, Camera as CameraIcon, Link as LinkIcon, Clock, User, Globe } from "react-feather";
import { Camera } from "../../type/models";
import { formatDate } from "../../type/utils";

interface CameraListProps {
  cameras: Camera[];
  onSelectCamera: (camera: Camera) => void;
  selectedCamera?: Camera;
}

const CameraList: React.FC<CameraListProps> = ({ cameras, onSelectCamera, selectedCamera }) => {
  console.log(cameras);

  return (
    <Box 
      width="300px" 
      borderRight="1px solid" 
      borderColor="gray.200" 
      height="100%"
      overflowY="auto"
      mr={2}
      sx={{
        '&::-webkit-scrollbar': {
          width: '4px',
        },
        '&::-webkit-scrollbar-track': {
          background: 'transparent',
        },
        '&::-webkit-scrollbar-thumb': {
          background: 'gray.300',
          borderRadius: '4px',
        },
      }}
    >
      <Accordion allowToggle>
        {cameras.map(camera => (
          <AccordionItem 
            key={camera.id} 
            borderColor={selectedCamera?.id === camera.id ? 'blue.500' : 'gray.200'}
            borderWidth={selectedCamera?.id === camera.id ? '2px' : '1px'}
          >
            <AccordionButton 
              onClick={() => onSelectCamera(camera)}
              _hover={{ bg: 'gray.100' }}
            >
              <Flex align="center" flex="1">
                <CameraIcon />
                <Text ml={2} isTruncated>{camera.name}</Text>
              </Flex>
              <AccordionIcon />
            </AccordionButton>
            <AccordionPanel pb={4}>
              <VStack align="stretch" spacing={3}>
                <Flex alignItems="center">
                  <Server size={16} />
                  <Text ml={2} fontSize="sm" fontWeight="medium">IP Address:</Text>
                  <Text ml={1} fontSize="sm" isTruncated>{camera.ipAddress}</Text>
                </Flex>
                <Flex alignItems="center">
                  <LinkIcon size={16} />
                  <Text ml={2} fontSize="sm" fontWeight="medium">RTSP:</Text>
                  <Text ml={1} fontSize="sm" isTruncated>{camera.rtsp}</Text>
                </Flex>
                <Flex alignItems="center">
                  <LinkIcon size={16} />
                  <Text ml={2} fontSize="sm" fontWeight="medium">Status:</Text>
                  <Text ml={1} fontSize="sm" isTruncated>{camera.cameraStatus}</Text>
                </Flex>
                <Flex alignItems="center">
                  <Clock size={16} />
                  <Text ml={2} fontSize="sm" fontWeight="medium">Port:</Text>
                  <Text ml={1} fontSize="sm">{camera.port}</Text>
                </Flex>
                <Flex alignItems="center">
                  <User size={16} />
                  <Text ml={2} fontSize="sm" fontWeight="medium">Associated Users:</Text>
                  <Badge ml={1} colorScheme="blue" variant="subtle">
                    {camera.cameraUserList.length}
                  </Badge>
                </Flex>
                <Flex alignItems="center">
                  <Globe size={16} />
                  <Text ml={2} fontSize="sm" fontWeight="medium">Created:</Text>
                  <Text ml={1} fontSize="sm">{formatDate(camera.createdAt)}</Text>
                </Flex>
                <Flex alignItems="center">
                  <Clock size={16} />
                  <Text ml={2} fontSize="sm" fontWeight="medium">Updated:</Text>
                  <Text ml={1} fontSize="sm">{formatDate(camera.updatedAt)}</Text>
                </Flex>
              </VStack>
            </AccordionPanel>
          </AccordionItem>
        ))}
      </Accordion>
    </Box>
  );
};

export default CameraList;