import {
  Button,
  Input,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  VStack,
  useToast,
} from "@chakra-ui/react";
import { useState } from "react";// Đổi đường dẫn đúng với project của bạn
import { useAddSchedulingMutation } from "../../redux/service/camera";
import { formatDateTimeScheduling } from "../../type/utils";
import useCustomToast from "../../hook/useCustomToast";

interface AddSchedulingModalProps {
  isOpen: boolean;
  onClose: () => void;
  cameraId: string;
}

export const AddSchedulingModal: React.FC<AddSchedulingModalProps> = ({
  isOpen,
  onClose,
  cameraId,
}) => {
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");
  const showToast = useCustomToast();

  const [addScheduling, { isLoading }] = useAddSchedulingMutation();

  const handleSave = async () => {
    if (!startTime || !endTime) {
      showToast("Error", "Please provide start time and end time", "error");
      return;
    }

    try {
      await addScheduling({
        requestBody: {
          startTime: formatDateTimeScheduling(startTime),
          endTime: formatDateTimeScheduling(endTime),
          cameraId
        },
      }).unwrap();

      showToast("Success", "Create schedule successfully", "success");

      onClose();
      setStartTime("");
      setEndTime("");

    } catch (error: any) {
      showToast("Error", error.message, "error");
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>Add New Scheduling</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <VStack spacing={4}>
            <Input
              placeholder="Start Time"
              type="datetime-local"
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
            />
            <Input
              placeholder="End Time"
              type="datetime-local"
              value={endTime}
              onChange={(e) => setEndTime(e.target.value)}
            />
          </VStack>
        </ModalBody>
        <ModalFooter>
          <Button
            colorScheme="blue"
            mr={3}
            onClick={handleSave}
            isLoading={isLoading}
          >
            Save
          </Button>
          <Button variant="ghost" onClick={onClose}>
            Cancel
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};
