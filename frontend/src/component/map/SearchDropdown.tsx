import { Box, List, ListItem, Image, Text } from "@chakra-ui/react";
import { InfraObject } from "../../type/models";
import { useEffect } from "react";

interface SearchDropdownProps {
  objects: InfraObject[];
  onSelect: (object: InfraObject) => void;
  onSetObjects: (objects: InfraObject[]) => void;
  setObjects: (objects: InfraObject[]) => void;
}

const SearchDropdown: React.FC<SearchDropdownProps> = ({ objects, onSelect, onSetObjects, setObjects }) => {
  if (objects.length === 0) {
    return null;
  }

  // if (objects.length === 0) {
  //   return (
  //     <Box
  //       position="absolute"
  //       bg="white"
  //       boxShadow="md"
  //       borderRadius="md"
  //       w="full"
  //       mt={2}
  //       zIndex="dropdown"
  //       maxH="300px"
  //       overflowY="auto"
  //     >
  //       <Text>No objects found.</Text>
  //     </Box>
  //   );
  // }

  const handleChooseObject = (object: InfraObject) => {
    onSelect(object);
    onSetObjects([object]);
    setObjects([]);
  };

  return (
    <Box
      position="absolute"
      bg="white"
      boxShadow="md"
      borderRadius="md"
      w="full"
      mt={2}
      zIndex="dropdown"
      maxH="300px"
      overflowY="auto"
    >
      <List>
        {objects.map((obj) => (
          <ListItem
            key={obj.id}
            p={3}
            borderBottom="1px solid"
            borderColor="gray.200"
            _hover={{ bg: "gray.100", cursor: "pointer" }}
            onClick={() => handleChooseObject(obj)}
          >
            <Box display="flex" alignItems="center">
              <Box>
                <Text fontWeight="bold">{obj.name}</Text>
                <Text fontSize="sm" color="gray.500">
                  {obj.location}
                </Text>
              </Box>
            </Box>
          </ListItem>
        ))}
      </List>
    </Box>
  );
};

export default SearchDropdown;