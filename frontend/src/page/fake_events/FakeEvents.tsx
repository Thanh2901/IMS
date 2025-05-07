import FakeEventsTable from "../../component/fake_events/FakeEventsTable";
import useCustomToast from "../../hook/useCustomToast";
import { useGetFakeEventsQuery } from "../../redux/service/infrastructure";
import { Box, Heading, HStack, Spinner, Text, VStack } from "@chakra-ui/react";

const FakeEvents = () => {
  const showToast = useCustomToast();  
  const { data: fakeEvents, isLoading, isError } = useGetFakeEventsQuery();

  if (isLoading) {
    return (
      <Box textAlign="center" py={4}>
        <Spinner size="lg" color="blue.500" />
      </Box>
    );
  }

  if (isError) {
    return (
      <Box textAlign="center" py={4}>
        <Text color="red.500">Failed to load data. Please try again later.</Text>
      </Box>
    );
  }

  return (
    <VStack spacing={4} align="stretch" px={6} py={4} height={"full"}>
      <HStack spacing={4} justify="center" alignSelf={"flex-end"}>
      </HStack>
      {(fakeEvents && fakeEvents?.length > 0) ? (
        <FakeEventsTable items={fakeEvents} />
      ) : (
        <Text color="gray.500" textAlign="center">
          No events found.
        </Text>
      )}

    </VStack> 
  );
};

export default FakeEvents;