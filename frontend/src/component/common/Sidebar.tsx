import { Box, VStack, useColorModeValue, Flex, Heading, Text } from "@chakra-ui/react";
import SidebarItem, { SidebarItemData } from "./SidebarItem";
import { useLocation } from "react-router-dom";
import { Home, Map, Box as BoxIcon, Calendar, Monitor, FileText, Camera } from "react-feather";

interface SidebarProps {
  isOpen: boolean;
}

const Sidebar = ({ isOpen }: SidebarProps) => {
  const location = useLocation();

  const bgColor = useColorModeValue("gray.800", "gray.900");
  const borderColor = useColorModeValue("gray.700", "gray.700");
  const textColor = useColorModeValue("white", "white");
  const hoverBgColor = useColorModeValue("gray.700", "gray.700");
  const selectedBgColor = useColorModeValue("blue.600", "blue.700");
  const logoColor = useColorModeValue("blue.400", "blue.300");

  const items: SidebarItemData[] = [
    { name: "Dashboard", path: "/dashboard", icon: <Home size={20} /> },
    { name: "Map", path: "/map", icon: <Map size={20} /> },
    { name: "Infrastructures", path: "/objects", icon: <BoxIcon size={20} /> },
    { name: "Events", path: "/events", icon: <Calendar size={20} /> },
    { name: "Scheduling", path: "/monitor", icon: <Monitor size={20} /> },
    { name: "Report", path: "/report", icon: <FileText size={20} /> },
    { name: "Cameras", path: "/cameras", icon: <Camera size={20} /> },
    { name: "Fake Event", path: "/fake-events", icon: <Camera size={20} /> },
  ];

  return (
    <Box
      width="100%"
      height="100%"
      boxShadow="lg"
      bg={bgColor}
      display="flex"
      flexDirection="column"
      overflowY="auto"
      overflowX="hidden"
      transition="width 0.3s ease"
    >
      {/* Logo/App Name */}
      <Flex 
        py={6} 
        px={6} 
        alignItems="center" 
        justifyContent="center"
        borderBottom="1px solid"
        borderColor="whiteAlpha.200"
      >
        <Heading 
          size="md" 
          color={logoColor} 
          fontWeight="bold"
          letterSpacing="wide"
        >
          Elcom Infrastructure
        </Heading>
      </Flex>

      {/* Navigation Items */}
      <VStack spacing={2} align="stretch" flex={1} mt={6} px={3}>
        {items.map((item, idx) => (
          <SidebarItem
            key={idx}
            item={item}
            selected={location.pathname === item.path || location.pathname.startsWith(`${item.path}/`)}
            textColor={textColor}
            hoverBgColor={hoverBgColor}
            selectedBgColor={selectedBgColor}
          />
        ))}
      </VStack>

      {/* Footer with app version */}
      <Box p={4} mt="auto" borderTop="1px solid" borderColor="whiteAlpha.200">
        <Text fontSize="xs" color="whiteAlpha.600" textAlign="center">
          v1.0.0
        </Text>
      </Box>
    </Box>
  );
};

export default Sidebar;
