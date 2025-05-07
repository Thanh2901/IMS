import { Box, HStack, Text, Tooltip } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";

export interface SidebarItemData {
  name: string;
  path: string;
  icon: any;
}

interface SidebarItemProps {
  item: SidebarItemData;
  selected: boolean;
  textColor: string;
  hoverBgColor: string;
  selectedBgColor: string;
}

const SidebarItem: React.FC<SidebarItemProps> = ({
  item,
  selected,
  textColor,
  hoverBgColor,
  selectedBgColor,
}) => {
  const navigate = useNavigate();

  return (
    <Box
      p={3}
      borderRadius="md"
      cursor="pointer"
      bg={selected ? selectedBgColor : "transparent"}
      _hover={{
        bg: selected ? selectedBgColor : hoverBgColor,
        color: "white",
      }}
      transition="all 0.2s ease"
      position="relative"
      onClick={() => navigate(item.path)}
    >
      {selected && (
        <Box
          position="absolute"
          left={0}
          top="50%"
          transform="translateY(-50%)"
          h="60%"
          w="4px"
          bg="blue.400"
          borderRightRadius="md"
        />
      )}
      
      <HStack spacing={3} align="center">
        <Box color={textColor} opacity={selected ? 1 : 0.8}>
          {item.icon}
        </Box>
        <Text
          fontWeight={selected ? "semibold" : "medium"}
          color={textColor}
          opacity={selected ? 1 : 0.8}
        >
          {item.name}
        </Text>
      </HStack>
    </Box>
  );
};

export default SidebarItem;