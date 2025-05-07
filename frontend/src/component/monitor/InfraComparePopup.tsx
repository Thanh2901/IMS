import React from "react";
import {
  Box,
  Text,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalCloseButton,
  ModalBody,
  Image,
  SimpleGrid,
} from "@chakra-ui/react";
import { InfraObject } from "../../type/models";

interface ComparePopupProps {
  infraRecord: any;
  isOpen: boolean;
  onClose: () => void;
}

const InfoBox = ({ title, infraObject }: { title: string; infraObject: InfraObject }) => (
  <Box borderWidth="1px" borderRadius="md" p={4}>
    <Text fontWeight="bold" fontSize="lg" mb={2}>{title}</Text>
    <Text><strong>Status:</strong> {infraObject?.status || 'N/A'}</Text>
    <Text><strong>Confidence:</strong> {infraObject?.confidence?.toFixed(2) || 'N/A'}</Text>
    <Text><strong>Level:</strong> {infraObject?.level.toString()|| 'N/A'}</Text>
    <Box mt={3} borderWidth="1px" borderRadius="md" w="100%" h="200px" overflow="hidden" display="flex" justifyContent="center" alignItems="center">
      {infraObject?.image?.pathUrl ? (
        <Image
          src={`${infraObject?.image?.pathUrl}`}

          objectFit="contain"
          w="100%"
          h="100%"
        />
      ) : (
        <Text>No Image Available</Text>
      )}
    </Box>
  </Box>
);

const InfraComparePopup: React.FC<ComparePopupProps> = ({ infraRecord, isOpen, onClose }) => (
  <Modal isOpen={isOpen} onClose={onClose} size="4xl">
    <ModalOverlay />
    <ModalContent>
      <ModalHeader>Compare Infrastructure <br/> Location: {infraRecord.oldInfraObject.location}</ModalHeader>
      <ModalCloseButton />
      <ModalBody>
        <SimpleGrid columns={2} spacing={4}>
          <InfoBox title="Old Detect" infraObject={infraRecord.oldInfraObject} />
          <InfoBox title="New Detect" infraObject={infraRecord.newInfraObject} />
        </SimpleGrid>
      </ModalBody>
    </ModalContent>
  </Modal>
);

export default InfraComparePopup;