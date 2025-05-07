import { useState } from "react";
import {
  infrastructureApi,
  useMarkFakeEventMutation,
} from "../../redux/service/infrastructure";
import {
  Button,
  Spinner,
  Box,
  Text,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalCloseButton,
  ModalFooter,
} from "@chakra-ui/react";
import { useDispatch } from "react-redux";
import ConfirmModal from "../common/ConfirmModal";

interface CheckEventPopupProps {
  videoUrl: string;
  eventId: string;
  isOpen: boolean;
  onClose: () => void;
}

const CheckEventPopup: React.FC<CheckEventPopupProps> = ({
  videoUrl,
  isOpen,
  onClose,
  eventId,
}) => {
  const [markEvent, { isLoading, isError }] = useMarkFakeEventMutation();
  const [isConfirmOpen, setConfirmOpen] = useState(false);
  const dispatch = useDispatch();

  const handleConfirmClose = () => setConfirmOpen(false);
  const handleConfirmOpen = () => setConfirmOpen(true);

  const handleMarkEvent = async () => {
    try {
      await markEvent(eventId);

      dispatch(infrastructureApi.util.invalidateTags(["Objects"]));
      dispatch(infrastructureApi.util.invalidateTags(["Events"]));

      setConfirmOpen(false);
      onClose();
    } catch (error) {
      console.error("Error marking event as fake", error);
    }
  };

  return (
    <>
      <Modal isOpen={isOpen} onClose={onClose} size="6xl">
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Check Event</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <Box
              position="relative"
              width="100%"
              paddingTop="56.25%" // 16:9 ratio (9/16 = 0.5625)
              backgroundColor="black"
            >
              <video
                controls
                src={videoUrl}
                autoPlay
                style={{
                  position: "absolute",
                  top: 0,
                  left: 0,
                  width: "100%",
                  height: "100%",
                  objectFit: "contain",
                  backgroundColor: "black",
                }}
              />
            </Box>

            <Box textAlign="center" mt={4}>
              <Button
                colorScheme="red"
                onClick={handleConfirmOpen}
                isDisabled={isLoading}
              >
                {isLoading ? <Spinner size="sm" /> : "Report as False"}
              </Button>
              {isError && (
                <Text mt={2} color="red.500">
                  Failed to mark event
                </Text>
              )}
            </Box>
          </ModalBody>
        </ModalContent>
      </Modal>

      <ConfirmModal
        isOpen={isConfirmOpen}
        onClose={handleConfirmClose}
        onConfirm={handleMarkEvent}
        title="Confirm report event as false"
        description="Are you sure you want to mark this event as fake?"
        isLoading={isLoading}
      />
    </>
  );
};

export default CheckEventPopup;
