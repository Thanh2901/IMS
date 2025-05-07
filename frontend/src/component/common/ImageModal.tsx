import { Modal, ModalOverlay, ModalContent, ModalHeader, ModalCloseButton, ModalBody, Image, Box, Text } from "@chakra-ui/react";
import React from "react";

interface ImageModalProps {
  isOpen: boolean;
  onClose: () => void;
  imageUrl?: string;
  title?: string;
}

const ImageModal: React.FC<ImageModalProps> = ({ isOpen, onClose, imageUrl, title }) => {
  return (
    <Modal isOpen={isOpen} onClose={onClose} size="5xl" isCentered>
      <ModalOverlay bg="blackAlpha.700" backdropFilter="blur(5px)" />
      <ModalContent bg="white" borderRadius="xl" overflow="hidden">
        <ModalHeader fontSize="md" pb={2}>{title}</ModalHeader>
        <ModalCloseButton />
        <ModalBody p={0}>
          <Box
            w="full"
            h="600px"
            bg="black" // Nền màu đen
            display="flex"
            alignItems="center"
            justifyContent="center"
          >
            {imageUrl ? (
              <Image
                src={imageUrl}
                alt={title}
                maxW="100%"
                maxH="100%"
                objectFit="contain"
              />
            ) : (
              <Text color="gray.400" fontSize="lg">Không có hình ảnh</Text>
            )}
          </Box>
        </ModalBody>
      </ModalContent>
    </Modal>
  );
};

export default ImageModal;
