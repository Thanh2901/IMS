
import { useForm } from "react-hook-form";
import { Flex, FormControl, FormErrorMessage, Heading, Select, Text } from "@chakra-ui/react";
import { Camera, InfraCategory, InfraStatus, ObjectFilterRequest } from "../../type/models";
import { useEffect } from "react";

interface ObjectsFilterProps {
  cameras: Camera[];
  filterCriteria: ObjectFilterRequest;
  setFilterCriteria: (criteria: ObjectFilterRequest) => void;
  onClear: () => void;
}

const ObjectsFilter = ({ filterCriteria, setFilterCriteria, onClear, cameras }: ObjectsFilterProps) => {
  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm<ObjectFilterRequest>({
    defaultValues: filterCriteria,
  });

  // useEffect(() => {
  //   if (cameras.length > 0) {
  //     setValue("cameraId", cameras[0].id);
  //     console.log(cameras[0].id);
  //   }
  // }, [cameras, setValue]);

  const handleClearFilters = () => {
    reset();
    onClear();
  };

  const handleChange = (field: keyof ObjectFilterRequest, value: string | number | null) => {
    setFilterCriteria({
      ...filterCriteria,
      [field]: value,
    });
  };

  return (
    <Flex width={"full"} align="center" justify={"right"}>
      <form>
        <Flex gap={5} align="center">
          {/* Status Filter */}
          <FormControl isInvalid={!!errors.status} flex={1}>
            <Flex align="center" gap={2}>
              {/* <Filter size={18} color="#3182CE" /> */}
              <Text fontSize="sm" fontWeight="semibold" color="gray.700">
                Status
              </Text>
              <Select
                {...register("status")}
                placeholder="All"
                onChange={(e) => handleChange("status", e.target.value || null)}
                variant="filled"
                borderRadius="md"
                _hover={{ bg: "gray.100" }}
                _focus={{ bg: "gray.100", borderColor: "blue.500" }}
                width={"120px"}
              >
                {Object.values(InfraStatus).map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </Select>
            </Flex>
            <FormErrorMessage>
              {errors.status && errors.status.message}
            </FormErrorMessage>
          </FormControl>

          {/* Camera Filter */}
          <FormControl isInvalid={!!errors.cameraId} flex={1}>
            <Flex align="center" gap={2}>
              {/* <Filter size={18} color="#3182CE" /> */}
              <Text fontSize="sm" fontWeight="semibold" color="gray.700">
                Camera
              </Text>
              <Select
                {...register("cameraId")}
                placeholder="All"
                onChange={(e) => handleChange("cameraId", e.target.value || null)}
                variant="filled"
                borderRadius="md"
                _hover={{ bg: "gray.100" }}
                _focus={{ bg: "gray.100", borderColor: "blue.500" }}
                width={"120px"}
              >
                {cameras.map((cam, idx) => (
                  <option key={idx} value={cam.id}>
                    {cam.name}
                  </option>
                ))}
              </Select>
            </Flex>
            <FormErrorMessage>
              {errors.status && errors.status.message}
            </FormErrorMessage>
          </FormControl>

          {/* Category Filter */}
          <FormControl isInvalid={!!errors.category} flex={1}>
            <Flex align="center" gap={2}>
              {/* <List size={18} color="#3182CE" /> */}
              <Text fontSize="sm" fontWeight="semibold" color="gray.700">
                Category
              </Text>
              <Select
                {...register("category")}
                placeholder="All"
                onChange={(e) => handleChange("category", e.target.value || null)}
                variant="filled"
                borderRadius="md"
                _hover={{ bg: "gray.100" }}
                _focus={{ bg: "gray.100", borderColor: "blue.500" }}
                width={"120px"}
              >
                {Object.values(InfraCategory).map((category) => (
                  <option key={category} value={category}>
                    {category.toUpperCase()}
                  </option>
                ))}
              </Select>
            </Flex>
            <FormErrorMessage>
              {errors.category && errors.category.message}
            </FormErrorMessage>
          </FormControl>

        </Flex>
      </form>
    </Flex>
  );
};

export default ObjectsFilter;