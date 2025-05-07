import React from 'react';
import {
  Box,
  Text,
  Badge,
  Flex,
  Grid,
  GridItem,
  Container,
  Divider,
  Spinner,
  Button,
  Image,
  useDisclosure,
  AspectRatio,
  Heading,
  VStack,
  HStack,
  Icon,
} from '@chakra-ui/react';
import { useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { AlertCircle, ArrowRight, Clock, Tag, MapPin, Camera } from 'react-feather';
import { useGetNotificationQuery } from '../../redux/service/notification';
import { formatDate, parseAdditionalData } from '../../type/utils';
import ImageModal from '../../component/common/ImageModal';

const NotificationDetails = () => {
  const { notificationId } = useParams<{ notificationId: string }>();
  const navigate = useNavigate();
  const { isOpen, onOpen, onClose } = useDisclosure();

  const {
    data: notification,
    isLoading,
    isError,
  } = useGetNotificationQuery(notificationId ?? '');

  console.log("Notification: ", notification);

  useEffect(() => {
    if (isError || (!isLoading && !notification)) {
      navigate('/not-found');
    }
  }, [isError, isLoading, notification, navigate]);

  if (isLoading) {
    return (
      <Flex justify="center" align="center" minH="50vh">
        <Spinner size="xl" thickness="4px" color="blue.500" />
      </Flex>
    );
  }

  if (!notification) return null;
  const additionalData = parseAdditionalData(notification);
  console.log("Additional data: ", additionalData);

  return (
    <Container maxW="100%" >
      <Box 
        w="100%" 
        bg="white"
        borderRadius="xl"
        boxShadow="lg"
        overflow="hidden"
      >
        {/* Header with title and status */}
        <Box bg="blue.50" p={3} borderBottom="1px" borderColor="blue.100">
          <Flex 
            direction={{ base: "column", md: "row" }}
            justify="space-between" 
            align={{ base: "start", md: "center" }}
          >
            <VStack spacing={1} align="start">
              <Heading 
                size="lg"
                color="blue.800"
              >
                {notification.title}
              </Heading>
              <HStack spacing={2}>
                <Icon as={Clock} size="16" color="gray.500" />
                <Text color="gray.500" fontSize="sm">
                  {formatDate(notification.createdAt)}
                </Text>
              </HStack>
            </VStack>
            <Badge 
              colorScheme={notification.isRead ? 'green' : 'blue'} 
              fontSize="md"
              px={4}
              py={2}
              borderRadius="full"
              mt={{ base: 4, md: 0 }}
            >
              {notification.isRead ? 'Read' : 'Unread'}
            </Badge>
          </Flex>
        </Box>

        {/* Main content */}
        <Grid templateColumns={{ base: "1fr", lg: "1fr 400px" }} gap={8} p={6}>
          {/* Left side - Notification content */}
          <GridItem>
            <VStack spacing={6} align="stretch">
              <Box
                bg="gray.50"
                p={6}
                borderRadius="lg"
                border="1px"
                borderColor="gray.200"
                minHeight="150px"
              >
                <Text fontSize="md" color="gray.700" whiteSpace="pre-line" lineHeight="tall">
                  {notification.body}
                </Text>
              </Box>

              {additionalData && (
                <Box
                  borderWidth="1px"
                  borderRadius="lg"
                  overflow="hidden"
                  boxShadow="sm"
                >
                  <Box bg="gray.50" p={4} borderBottomWidth="1px">
                    <Flex justify="space-between" align="center">
                      <Heading size="sm" color="gray.700">
                        Details
                      </Heading>
                      <Badge colorScheme={getStatusColor(additionalData.status)} px={3} py={1} borderRadius="full">
                        {additionalData.status || 'N/A'}
                      </Badge>
                    </Flex>
                  </Box>

                  <Box p={4}>
                    <Grid templateColumns="1fr 1fr" gap={4}>
                      <DetailItem
                        icon={Camera}
                        label="Camera ID"
                        value={additionalData.cameraId}
                      />
                      <DetailItem
                        icon={MapPin}
                        label="Location"
                        value={additionalData.location}
                      />
                      <DetailItem
                        icon={AlertCircle}
                        label="Object"
                        value={additionalData.name}
                      />
                      <DetailItem
                        icon={Tag}
                        label="Type"
                        value={additionalData.type}
                      />
                      <DetailItem
                        icon={Tag}
                        label="Category"
                        value={additionalData.category}
                        colSpan={2}
                      />
                    </Grid>
                  </Box>
                </Box>
              )}

              {/* Event details button */}
              {/* {additionalData && (
                <Button
                  as={Link}
                  to={`/events/${additionalData.eventId}`}
                  colorScheme="blue"
                  rightIcon={<ArrowRight size={16} />}
                  size="lg"
                  width={{ base: "full", md: "auto" }}
                  boxShadow="sm"
                  _hover={{ boxShadow: "md", transform: "translateY(-2px)" }}
                  transition="all 0.2s"
                >
                  View Event Details
                </Button>
              )} */}

            </VStack>
          </GridItem>

          {/* Right side - Image and details */}
          {additionalData && (
            <GridItem>
              <VStack spacing={6} align="stretch">
                {/* Image display */}
                {additionalData.urlImage && (
                  <Box 
                    borderRadius="lg" 
                    overflow="hidden" 
                    boxShadow="md"
                    transition="all 0.2s"
                    _hover={{ boxShadow: "lg" }}
                  >
                      <Image
                        src={additionalData.urlImage}
                        alt="Notification image"
                        objectFit="contain"
                        onClick={onOpen}
                        cursor="pointer"
                        fallback={
                          <Flex bg="gray.100" justify="center" align="center" h="100%">
                            <Icon as={AlertCircle} w={12} h={12} color="gray.400" />
                          </Flex>
                        }
                      />
                  </Box>
                )}

                {/* Detail items */}
                {/* <Box 
                  borderWidth="1px" 
                  borderRadius="lg" 
                  overflow="hidden"
                  boxShadow="sm"
                >
                  <Box bg="gray.50" p={4} borderBottomWidth="1px">
                    <Flex justify="space-between" align="center">
                      <Heading size="sm" color="gray.700">
                        Details
                      </Heading>
                      <Badge colorScheme={getStatusColor(additionalData.status)} px={3} py={1} borderRadius="full">
                        {additionalData.status || 'N/A'}
                      </Badge>
                    </Flex>
                  </Box>
                  
                  <Box p={4}>
                    <Grid templateColumns="1fr 1fr" gap={4}>
                      <DetailItem 
                        icon={Camera} 
                        label="Camera ID" 
                        value={additionalData.cameraId} 
                      />
                      <DetailItem 
                        icon={MapPin} 
                        label="Location" 
                        value={additionalData.location} 
                      />
                      <DetailItem 
                        icon={AlertCircle} 
                        label="Object" 
                        value={additionalData.name} 
                      />
                      <DetailItem 
                        icon={Tag} 
                        label="Type" 
                        value={additionalData.type} 
                      />
                      <DetailItem 
                        icon={Tag} 
                        label="Category" 
                        value={additionalData.category} 
                        colSpan={2}
                      />
                    </Grid>
                  </Box>
                </Box> */}

              {/* Event details button */}
              {additionalData && (
                <Button
                  as={Link}
                  to={`/events/${additionalData.eventId}`}
                  colorScheme="blue"
                  rightIcon={<ArrowRight size={16} />}
                  size="lg"
                  width={{ base: "full", md: "auto" }}
                  boxShadow="sm"
                  _hover={{ boxShadow: "md", transform: "translateY(-2px)" }}
                  transition="all 0.2s"
                >
                  View Event Details
                </Button>
              )}
              </VStack>
            </GridItem>
          )}
        </Grid>
      </Box>
      
      {/* Image Modal */}
      <ImageModal isOpen={isOpen} onClose={onClose} imageUrl={additionalData?.urlImage || ''} title="Image" />
    </Container>
  );
};

// Helper function to determine status color
const getStatusColor = (status?: string) => {
  if (!status) return 'gray';
  
  const statusLower = status.toLowerCase();
  if (statusLower.includes('active') || statusLower.includes('success')) return 'green';
  if (statusLower.includes('warning')) return 'yellow';
  if (statusLower.includes('error') || statusLower.includes('danger')) return 'red';
  if (statusLower.includes('info')) return 'blue';
  
  return 'gray';
};

const DetailItem = ({ icon, label, value, colSpan }: { icon: any; label: string; value?: string; colSpan?: number }) => (
  <GridItem colSpan={colSpan}>
    <HStack spacing={2} align="center">
      <Icon as={icon} size="16" color="blue.500" />
      <VStack spacing={0} align="start">
        <Text fontSize="xs" color="gray.500" fontWeight="medium">
          {label}
        </Text>
        <Text fontSize="sm" fontWeight="semibold" noOfLines={1} title={value || 'N/A'}>
          {value || 'N/A'}
        </Text>
      </VStack>
    </HStack>
  </GridItem>
);

export default NotificationDetails;