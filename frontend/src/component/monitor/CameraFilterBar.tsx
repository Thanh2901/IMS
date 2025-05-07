import {
  Box,
  Button,
  Flex,
  HStack,
  Input,
  InputGroup,
  InputRightElement,
  IconButton,
  Select,
  Text,
} from "@chakra-ui/react";
import { Camera } from "../../type/models";
import { X } from "react-feather";

interface CameraFilterBarProps {
  cameras: Camera[];
  cameraId: string;
  startDate: string;
  endDate: string;
  filterDate: string;
  onChangeCamera: (id: string) => void;
  onChangeStartDate: (date: string) => void;
  onChangeEndDate: (date: string) => void;
  onChangeFilterDate: (date: string) => void;
  onAddNewClick: () => void;
  view: "list" | "table";
  onSwitchView: () => void;
}

export const CameraFilterBar: React.FC<CameraFilterBarProps> = ({
  cameras,
  cameraId,
  startDate,
  endDate,
  filterDate,
  onChangeCamera,
  onChangeStartDate,
  onChangeEndDate,
  onChangeFilterDate,
  onAddNewClick,
  view,
  onSwitchView,
}) => (
  <Flex mb={6} align="flex-end" flexWrap="wrap" justify="space-between" w="100%">
    <HStack spacing={4} align="flex-end" flexWrap="wrap">
      <Box>
        <Text fontWeight="medium" mb={2}>
          Camera
        </Text>
        <Select value={cameraId} onChange={(e) => onChangeCamera(e.target.value)} minW="200px">
          {cameras.map((camera) => (
            <option key={camera.id} value={camera.id}>
              {camera.name}
            </option>
          ))}
        </Select>
      </Box>

      <Box>
        <Text fontWeight="medium" mb={2}>
          Start Time
        </Text>
        <InputGroup minW="240px">
          <Input
            type="datetime-local"
            value={startDate}
            onChange={(e) => onChangeStartDate(e.target.value)}
          />
          {startDate && (
            <InputRightElement>
              <IconButton
                aria-label="Clear start date"
                icon={<X size={16} />}
                size="xs"
                onClick={() => onChangeStartDate("")}
                variant="ghost"
              />
            </InputRightElement>
          )}
        </InputGroup>
      </Box>

      <Box>
        <Text fontWeight="medium" mb={2}>
          End Time
        </Text>
        <InputGroup minW="240px">
          <Input
            type="datetime-local"
            value={endDate}
            onChange={(e) => onChangeEndDate(e.target.value)}
          />
          {endDate && (
            <InputRightElement>
              <IconButton
                aria-label="Clear end date"
                icon={<X size={16} />}
                size="xs"
                onClick={() => onChangeEndDate("")}
                variant="ghost"
              />
            </InputRightElement>
          )}
        </InputGroup>
      </Box>

      <Box>
        <Text fontWeight="medium" mb={2}>
          Select Date
        </Text>
        <InputGroup minW="200px">
          <Input
            type="date"
            value={filterDate}
            onChange={(e) => onChangeFilterDate(e.target.value)}
          />
          {filterDate && (
            <InputRightElement>
              <IconButton
                aria-label="Clear filter date"
                icon={<X size={16} />}
                size="xs"
                onClick={() => onChangeFilterDate("")}
                variant="ghost"
              />
            </InputRightElement>
          )}
        </InputGroup>
      </Box>
    </HStack>

    <HStack spacing={2} mt={{ base: 4, md: 0 }}>
      <Button colorScheme="green" onClick={onAddNewClick} height="38px">
        Add
      </Button>
      <Button colorScheme="blue" onClick={onSwitchView} height="38px">
        Switch to {view === "list" ? "Table" : "List"} View
      </Button>
    </HStack>
  </Flex>
);
