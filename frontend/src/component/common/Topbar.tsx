import {
  Box,
  Flex,
  Avatar,
  Text,
  IconButton,
  Spinner,
  useColorModeValue,
  Badge,
  Popover,
  PopoverTrigger,
  PopoverContent,
  PopoverBody,
  Portal,
  Heading,
} from "@chakra-ui/react";
import { Bell, ChevronDown, ChevronUp, Menu } from "react-feather";
import { useGetMeQuery } from "../../redux/service/user";
import { useGetNotificationsQuery } from "../../redux/service/notification";
import { useEffect, useState } from "react";
import DropdownMenu from "./DropdownMenu";
import { useLocation, useNavigate } from "react-router-dom";
import NotificationList from "../notification/NotificationList";

interface TopbarProps {
  onToggleSidebar: () => void;
  isSidebarOpen: boolean;
}

const Topbar = ({ onToggleSidebar, isSidebarOpen }: TopbarProps) => {
  const location = useLocation();
  const [showDropdown, setShowDropdown] = useState<boolean>(false);
  const [showNotifications, setShowNotifications] = useState<boolean>(false);
  const navigate = useNavigate();

  const {
    data: user,
    isLoading: isUserLoading,
    isError: isUserError
  } = useGetMeQuery();

  const {
    data: notifications,
    isLoading: isNotificationsLoading,
    isError: isNotificationError,
  } = useGetNotificationsQuery();

  useEffect(() => {
    setShowDropdown(false);
    setShowNotifications(false);
  }, [location]);

  const notificationCount = notifications?.length || 0;

  const bgColor = useColorModeValue("white", "gray.800");
  const textColor = useColorModeValue("gray.700", "gray.200");
  const shadowColor = useColorModeValue("rgba(0, 0, 0, 0.1)", "rgba(0, 0, 0, 0.4)");

  const toggleDropdown = () => {
    setShowDropdown(!showDropdown);
  };

  const closeDropdown = () => {
    setShowDropdown(false);
  };

  const unreadNotificationCount =
    notifications?.filter((notification) => !notification.isRead).length || 0;

  if (isUserLoading) {
    return (
      <Box w="full" py={3} px={4} bg={bgColor} borderBottom="1px solid" borderColor="gray.200" boxShadow="sm">
        <Flex align="center" justify="center">
          <Spinner size="md" />
        </Flex>
      </Box>
    );
  }

  if (isUserError) {
    localStorage.removeItem("access_token");
    navigate("/login");
    return (
      <Box w="full" py={3} px={4} bg={bgColor} borderBottom="1px solid" borderColor="gray.200" boxShadow="sm">
        <Flex align="center" justify="center">
          <Text color="red.500">Error while loading data</Text>
        </Flex>
      </Box>
    );
  }

  const currentPage = location.pathname.split('/')[1] || 'dashboard';
  const pageTitle = currentPage.charAt(0).toUpperCase() + currentPage.slice(1);

  return (
    <Box 
      w="full" 
      py={3} 
      px={4} 
      bg={bgColor} 
      borderBottom="1px solid" 
      borderColor="gray.200"
      boxShadow="sm"
      position="sticky"
      top="0"
      zIndex={10}
    >
      <Flex align="center" justify="space-between">
        <Flex align="center">
          <IconButton
            aria-label="Toggle Sidebar"
            icon={<Menu size={20} />}
            variant="ghost"
            mr={4}
            onClick={onToggleSidebar}
          />
          <Heading size="md" color={textColor}>
            {pageTitle}
          </Heading>
        </Flex>

        <Flex align="center" gap={4}>
          <Box position="relative">
            <Popover
              isOpen={showNotifications}
              onClose={() => setShowNotifications(false)}
              placement="bottom-end"
            >
              <PopoverTrigger>
                <IconButton
                  aria-label="Notifications"
                  icon={<Bell size={20} />}
                  variant="ghost"
                  color={textColor}
                  _hover={{ bg: "gray.100" }}
                  onClick={() => setShowNotifications(!showNotifications)}
                />
              </PopoverTrigger>
              <Portal>
                <PopoverContent 
                  width="400px" 
                  boxShadow={`0 4px 6px -1px ${shadowColor}, 0 2px 4px -1px ${shadowColor}`}
                  border="none"
                >
                  <PopoverBody p={0}>
                    {isNotificationsLoading ? (
                      <Flex justify="center" align="center" h="200px">
                        <Spinner />
                      </Flex>
                    ) : isNotificationError ? (
                      <Flex justify="center" align="center" h="200px">
                        <Text color="red.500">Error loading notifications</Text>
                      </Flex>
                    ) : (
                      <NotificationList notifications={notifications || []} />
                    )}
                  </PopoverBody>
                </PopoverContent>
              </Portal>
            </Popover>

            {!isNotificationsLoading && !isNotificationError && unreadNotificationCount > 0 && (
              <Badge
                position="absolute"
                top="-1"
                right="-1"
                colorScheme="red"
                borderRadius="full"
                fontSize="xs"
                px={1.5}
                py={0.5}
              >
                {unreadNotificationCount}
              </Badge>
            )}
          </Box>

          <Flex align="center" gap={2} position="relative">
            <Avatar size="sm" name={user?.fullName} />
            <Text fontWeight="bold" color={textColor} display={{ base: "none", md: "block" }}>
              {user?.fullName}
            </Text>
            <IconButton
              aria-label="Dropdown Menu"
              icon={showDropdown ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
              variant="ghost"
              color={textColor}
              size="sm"
              onClick={toggleDropdown}
            />
            {showDropdown && <DropdownMenu onClose={closeDropdown} />}
          </Flex>
        </Flex>
      </Flex>
    </Box>
  );
};

export default Topbar;