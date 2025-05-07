import React from 'react';
import { Box, Flex, Text, Icon, useColorModeValue, SlideFade } from '@chakra-ui/react';
import { Notification } from '../../type/models';
import { Mail } from 'react-feather';
import { formatDate } from '../../type/utils';
import { useNavigate } from 'react-router-dom';
import { useReadNotificationMutation } from '../../redux/service/notification';

interface NotificationProps {
  item: Notification;
}

const NotificationItem: React.FC<NotificationProps> = ({ item }) => {
  const navigate = useNavigate();
  const bgColor = useColorModeValue(item.isRead ? 'gray.50' : 'blue.50', item.isRead ? 'gray.700' : 'blue.900');
  const iconColor = useColorModeValue('blue.500', 'blue.200');
  const borderColor = useColorModeValue('gray.200', 'gray.600');
  const textColor = useColorModeValue('gray.700', 'gray.200');
  const secondaryTextColor = useColorModeValue('gray.500', 'gray.400');
  const [readNotification] = useReadNotificationMutation();

  const handleClickNotification = async () => {
    await readNotification(item.id);
    navigate(`/notifications/${item.id}`);
  };

  return (
    <SlideFade in={true} offsetY="20px">
      <Box
        bg={bgColor}
        p={4}
        borderRadius="md"
        mb={3}
        borderWidth="1px"
        borderColor={borderColor}
        transition="all 0.2s"
        _hover={{
          transform: 'translateY(-2px)',
          boxShadow: 'lg',
        }}
        cursor={"pointer"}
        onClick={handleClickNotification}
      >
        <Flex align="center">
          <Icon as={Mail} color={iconColor} mr={3} boxSize={5} />
          <Box flex={1}>
            <Text fontWeight="bold" color={textColor}>
              {item.title}
            </Text>
            <Text fontSize="sm" color={secondaryTextColor} mt={1}>
              {formatDate(item.createdAt)}
            </Text>
            {item.isRead && (
              <Text fontSize="xs" color={secondaryTextColor} mt={1}>
                Read at: {formatDate(item.readAt)}
              </Text>
            )}
          </Box>
        </Flex>
      </Box>
    </SlideFade>
  );
};

export default NotificationItem;