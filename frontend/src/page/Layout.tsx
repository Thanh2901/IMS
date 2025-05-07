import { Outlet, useNavigate } from 'react-router-dom';
import useAuth from '../hook/useAuth';
import { useEffect, useState } from 'react';
import Sidebar from '../component/common/Sidebar';
import { Flex, Box, IconButton, useDisclosure, useBreakpointValue } from '@chakra-ui/react';
import Topbar from '../component/common/Topbar';
import { Menu } from 'react-feather';

const Layout = () => {
  const { isLoggedIn } = useAuth();
  const navigate = useNavigate();
  const { isOpen, onToggle } = useDisclosure({ defaultIsOpen: true });
  const isMobile = useBreakpointValue({ base: false, md: false });

  useEffect(() => {
    if (!isLoggedIn()) {
      navigate('/login');
    }
  }, [navigate, isLoggedIn]);

  // Auto-close sidebar on mobile view
  useEffect(() => {
    if (isMobile && isOpen) {
      onToggle();
    }
  }, [isMobile]);

  return (
    <Flex direction="row" w="full" h="100vh">
      {/* Sidebar */}
      <Box 
        position={{ base: "fixed", md: "relative" }}
        left={0}
        top={0}
        h="100vh"
        zIndex={20}
        transform={{ 
          base: isOpen ? "translateX(0)" : "translateX(-100%)", 
          md: "translateX(0)" 
        }}
        transition="all 0.05s ease"
        width={isOpen ? "240px" : "0px"}
      >
        <Sidebar isOpen={isOpen} />
      </Box>

      {/* Main Content */}
      <Flex 
        direction="column" 
        flex={1} 
        ml={{ base: 0, md: isOpen ? "0" : "0" }}
        transition="margin-left 0.3s ease"
        w={isOpen ? { base: "100%", md: "calc(100% - 240px)" } : "100%"}
      >
        <Topbar onToggleSidebar={onToggle} isSidebarOpen={isOpen} />
        
        {/* Overlay for mobile when sidebar is open */}
        {isMobile && isOpen && (
          <Box 
            position="fixed"
            top={0}
            left={0}
            right={0}
            bottom={0}
            bg="blackAlpha.600"
            zIndex={15}
            onClick={onToggle}
          />
        )}
        
        <Box flex={1} overflowY="auto" p={4}>
          <Outlet />
        </Box>
      </Flex>
    </Flex>
  );
};

export default Layout;