import React, { useState } from 'react';
import { 
  VStack, 
  Text, 
  useColorModeValue, 
  Box,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel
} from '@chakra-ui/react';
import { Notification } from '../../type/models';
import NotificationItem from './NotificationItem';

interface NotificationListProps {
  notifications: Notification[];
}

const NotificationList: React.FC<NotificationListProps> = ({ notifications }) => {
  const [activeTab, setActiveTab] = useState(0);

  const bgColor = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.100', 'gray.700');
  const emptyTextColor = useColorModeValue('gray.500', 'gray.400');
  const scrollbarThumbColor = useColorModeValue('gray.400', 'gray.600');
  const scrollbarTrackColor = useColorModeValue('gray.100', 'gray.700');

  const readNotifications = notifications.filter(n => n.isRead);
  const unreadNotifications = notifications.filter(n => !n.isRead);

  const renderNotificationList = (notificationsList: Notification[]) => (
    <VStack 
      p={4}
      spacing={0} 
      align="stretch" 
      maxHeight="400px" 
      overflowY="auto" 
      sx={{
        '&::-webkit-scrollbar': {
          width: '8px',
        },
        '&::-webkit-scrollbar-track': {
          background: scrollbarTrackColor,
        },
        '&::-webkit-scrollbar-thumb': {
          background: scrollbarThumbColor,
          borderRadius: '4px',
        },
      }}
    >
      {notificationsList.length === 0 ? (
        <Box p={4}>
          <Text 
            textAlign="center" 
            color={emptyTextColor} 
            fontSize="sm"
          >
            No notifications found
          </Text>
        </Box>
      ) : (
        notificationsList.map((notification, index) => (
          <Box 
            key={notification.id}
            borderBottom={index !== notificationsList.length - 1 ? "1px solid" : "none"}
            borderColor={borderColor}
            _hover={{ bg: useColorModeValue('gray.50', 'gray.700') }}
            transition="background-color 0.2s"
          >
            <NotificationItem item={notification} />
          </Box>
        ))
      )}
    </VStack>
  );

  return (
    <Box 
      bg={bgColor} 
      borderRadius="md" 
      overflow="hidden"
    >
      <Tabs 
        isFitted 
        variant="enclosed" 
        onChange={(index) => setActiveTab(index)}
      >
        <TabList>
          <Tab>
            Unread 
            {unreadNotifications.length > 0 && (
              <Text 
                ml={2} 
                bg="red.500" 
                color="white" 
                borderRadius="full" 
                px={2} 
                fontSize="xs"
              >
                {unreadNotifications.length}
              </Text>
            )}
          </Tab>
          <Tab>
            Read
            {readNotifications.length > 0 && (
              <Text 
                ml={2} 
                bg="gray.500" 
                color="white" 
                borderRadius="full" 
                px={2} 
                fontSize="xs"
              >
                {readNotifications.length}
              </Text>
            )}
          </Tab>
        </TabList>

        <TabPanels>
          <TabPanel p={0}>
            {renderNotificationList(unreadNotifications)}
          </TabPanel>
          <TabPanel p={0}>
            {renderNotificationList(readNotifications)}
          </TabPanel>
        </TabPanels>
      </Tabs>
    </Box>
  );
};

export default NotificationList;