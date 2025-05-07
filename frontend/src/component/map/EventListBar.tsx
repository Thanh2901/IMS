import { infrastructureApi, useCreateRepairEventMutation, useGetEventsByObjectIdQuery, useGetEventsQuery } from "../../redux/service/infrastructure";
import {
  Box,
  Divider,
  Heading,
  Spinner,
  Text,
  useColorModeValue,
  VStack,
  useToken,
  Button,
} from "@chakra-ui/react";
import { Calendar, Tool } from "react-feather";
import EventItem from "./EventItem";
import React from "react";
import useCustomToast from "../../hook/useCustomToast";
import { useDispatch } from "react-redux";

interface EventsListBarProps {
  objectId: string;
}

const EventListBar: React.FC<EventsListBarProps> = ({ objectId }) => {
  const showToast = useCustomToast();
  const [gray50, gray100, gray200, gray700] = useToken('colors', ['gray.50', 'gray.100', 'gray.200', 'gray.700']);
  const dispatch = useDispatch();

  const bg = useColorModeValue("white", gray50);
  const borderColor = useColorModeValue(gray100, gray200);

  const { data: events, isLoading } = useGetEventsByObjectIdQuery(objectId);
  // const { data: events, isLoading } = useGetEventsQuery(
  //   {
  //     requestBody: {
  //       infraObjectId: objectId,
  //     },
  //   },
  //   { skip: !objectId }
  // );

  const [createRepair, { isLoading: isCreatingRepair }] = useCreateRepairEventMutation();

  const handleCreateRepairEvent = async () => {
    try {
      await createRepair(objectId || "").unwrap();
      showToast("Repair event", "Create repair event successfully", "success");
      dispatch(infrastructureApi.util.invalidateTags(["Events"]));
    } catch (error: any) {
      showToast("Create failed", error.message, "error");
    }
  };

  return (
    <Box
      height="100%"
      display="flex"
      flexDirection="column"
      bg={bg}
      borderRadius="lg"
      borderWidth="2px"
      borderColor={borderColor}
      boxShadow="lg"
      transition="width 0.3s ease"
      position="relative"
    >
      {isLoading && (
        <Box display="flex" justifyContent="center" alignItems="center" height="100%">
          <Spinner color="blue.400" />
        </Box>
      )}
      {!objectId && (
        <Box p={6} bg={gray50} borderRadius="lg" borderWidth="1px" borderColor={borderColor} textAlign="center" height="100%">
          <Calendar size={24} color={gray700} style={{ margin: '0 auto', marginBottom: '12px' }} />
          <Text color="gray.500">Please select an object to view events</Text>
        </Box>
      )}
      {events?.length === 0 && (
        <Box p={6} bg={gray50} borderRadius="lg" borderWidth="1px" borderColor={borderColor} textAlign="center" height="100%">
          <Calendar size={24} color={gray700} style={{ margin: '0 auto', marginBottom: '12px' }} />
          <Text color="gray.500">No events found for this object</Text>
        </Box>
      )}
      <Box
        flex="1"
        overflowY="auto"
        maxHeight="calc(100vh - 200px)"
        css={{
          '&::-webkit-scrollbar': { width: '4px' },
          '&::-webkit-scrollbar-track': { background: 'transparent' },
          '&::-webkit-scrollbar-thumb': { background: gray200, borderRadius: '24px' },
        }}
      >
        <VStack spacing={0} p={6} align="stretch">
          <Box display="flex" alignItems="center" justifyContent="space-between" mb={4}>
            <Box display="flex" alignItems="center">
              <Calendar size={20} color={gray700} style={{ marginRight: '8px' }} />
              <Heading size="md" color={gray700}>
                Events Timeline
              </Heading>
            </Box>
            <Box>
              <Button
                size="sm"
                colorScheme="orange"
                onClick={handleCreateRepairEvent}
                flex={1}
                variant="solid"
                leftIcon={<Tool size={20} />}
                isLoading={isCreatingRepair}
              >
                Schedule Repair
              </Button>
            </Box>
          </Box>
          <Divider mb={4} />
          {events?.map((event, idx) => (
            <EventItem key={idx} item={event} isLast={idx === events.length - 1} isFirst={idx === 0} />
          ))}
        </VStack>
      </Box>
    </Box>

  );
};

export default EventListBar;