import {
  Button,
  Table,
  Tbody,
  Td,
  Th,
  Thead,
  Tr,
  useToast,
  HStack,
} from "@chakra-ui/react";
import { Scheduling } from "../../type/models";
import { useNavigate } from "react-router-dom";
import { useDeleteSchedulingMutation } from "../../redux/service/monitor";
import useCustomToast from "../../hook/useCustomToast";

interface SchedulingTableViewProps {
  schedules: Scheduling[];
}

export const SchedulingTableView: React.FC<SchedulingTableViewProps> = ({
  schedules,
}) => {
  const navigate = useNavigate();
  const showToast = useCustomToast();

  const handleViewDetails = (id: string) => {
    navigate(`/schedule/${id}`);
  };

  const [deleteScheduling] = useDeleteSchedulingMutation();

  const handleDelete = async (id: string) => {
    const confirmed = window.confirm("Are you sure you want to delete this schedule?");
    if (confirmed) {
      try {
        await deleteScheduling(id).unwrap();
        showToast("Success", "Deleted successfully", "success");

      } catch (error: any) {
        showToast("Error", "Deleted failed", "error");
      }
    }
  };

  const formatDate = (dateStr: string) =>
    new Date(dateStr).toLocaleDateString();

  const formatTime = (dateStr: string) =>
    new Date(dateStr).toLocaleTimeString();

  return (
    <Table variant="simple">
      <Thead>
        <Tr>
          <Th>Date</Th>
          <Th>Start Time</Th>
          <Th>End Time</Th>
          <Th>Video</Th>
          <Th>GPS</Th>
          <Th>Actions</Th>
        </Tr>
      </Thead>
      <Tbody>
        {schedules.map((schedule) => (
          <Tr key={schedule.id}>
            <Td>{formatDate(schedule.startTime)}</Td>
            <Td>{formatTime(schedule.startTime)}</Td>
            <Td>{formatTime(schedule.endTime)}</Td>
            <Td>{schedule.videoUrl ? "Yes" : "No"}</Td>
            <Td>{schedule.gpsLogsUrl ? "Yes" : "No"}</Td>
            <Td>
              <HStack spacing={5}>
                <Button
                  size="sm"
                  colorScheme="blue"
                  onClick={() => handleViewDetails(schedule.id)}
                >
                  View
                </Button>
                <Button
                  size="sm"
                  colorScheme="red"
                  onClick={() => handleDelete(schedule.id)}
                >
                  Delete
                </Button>
              </HStack>
            </Td>
          </Tr>
        ))}
      </Tbody>
    </Table>
  );
};
