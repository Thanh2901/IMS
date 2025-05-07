import { Box, List, ListItem, Avatar, Text, Badge, useColorModeValue, Flex, Divider } from "@chakra-ui/react";
import { Camera } from "../../type/models";

interface CameraListBarProps {
  cameras: Camera[];
  selectedCameraId: string | null;
  onCameraSelect: (cameraId: string | null) => void;
}

const CameraListBar = ({ cameras, selectedCameraId, onCameraSelect }: CameraListBarProps) => {
  const bg = useColorModeValue("white", "gray.800");
  const selectedBg = useColorModeValue("blue.50", "blue.900");
  
  return (
    <Box 
      minWidth={"200px"}
      h="full" 
      bg={bg} 
      p={4} 
      borderRadius="md" 
      borderWidth="1px" 
      boxShadow={"md"}
    >
      <Text fontSize="lg" fontWeight="medium">
        Camera List
      </Text>

      <Divider bgColor={"blue.500"} borderColor={"pink.500"} borderWidth={"1px"} mb={4} />
      
      <List spacing={2}>
        {cameras.map((camera) => (
          <ListItem
            key={camera.id}
            onClick={() => onCameraSelect(camera.id)}
            bg={camera.id === selectedCameraId ? selectedBg : "transparent"}
            p={3}
            borderRadius="md"
            cursor="pointer"
            transition="all 0.2s"
            _hover={{ bg: useColorModeValue("gray.50", "gray.700") }}
          >
            <Flex align="center" gap={3}>
              <Box flex={1}>
                <Text fontWeight="medium">{camera.name}</Text>
                <Text fontSize="sm" color="gray.500">
                  {camera.ipAddress}
                </Text>
              </Box>
              <Badge 
                colorScheme={camera.cameraUserList.length ? "green" : "gray"}
                variant="subtle"
              >
                {camera.cameraUserList.length ? "Active" : "Offline"}
              </Badge>
            </Flex>
          </ListItem>
        ))}
        
        {!cameras.length && (
          <Flex 
            justify="center" 
            py={8} 
            color="gray.500"
          >
            No cameras found
          </Flex>
        )}
      </List>
    </Box>
  );
};

export default CameraListBar;