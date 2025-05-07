import { Box, Table, Thead, Tbody, Tr, Th, Td, Badge } from "@chakra-ui/react";
import { FakeEvent } from "../../type/models";
import { formatDate } from "../../type/utils";

interface FakeEventsTableProps {
  items: FakeEvent[];
}

const FakeEventsTable: React.FC<FakeEventsTableProps> = ({ items }) => {
  return (
    <Box overflowX="auto" borderWidth="1px" borderRadius="lg" shadow="md">
      <Table variant="simple" minWidth="900px">
        <Thead bg="gray.100">
          <Tr>
            <Th width="180px" maxWidth="180px" textAlign="center">Time</Th>
            <Th width="140px" maxWidth="140px" textAlign="center">Name</Th>
            <Th width="220px" maxWidth="220px" textAlign="center">Status</Th>
            <Th width="100px" maxWidth="100px" textAlign="center">Location</Th>
          </Tr>
        </Thead>
        <Tbody>
          {items.length > 0 ? (
            items.map((item) => (
              <Tr key={item.id}>
                <Td width="180px" maxWidth="180px" textAlign="center">{formatDate(item.time)}</Td>
                <Td width="140px" maxWidth="140px" textAlign="center">{item.name}</Td>
                <Td width="220px" maxWidth="220px" textAlign="center">
                  <Badge colorScheme={item.status === "OK" ? "green" : "red"}>{item.status}</Badge>
                </Td>
                <Td width="100px" maxWidth="100px" textAlign="center">{item.location}</Td>
              </Tr>
            ))
          ) : (
            <Tr>
              <Td colSpan={5} textAlign="center" py={4} color="gray.500">
                No fake events available
              </Td>
            </Tr>
          )}
        </Tbody>
      </Table>
    </Box>
  );
};

export default FakeEventsTable;