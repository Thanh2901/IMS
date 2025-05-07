import { Text, Box, Table, Thead, Tbody, Tr, Th, Td, Badge, Image, useDisclosure, Input, Select, Flex, Button, AlertDialog, AlertDialogBody, AlertDialogFooter, AlertDialogHeader, AlertDialogContent, AlertDialogOverlay, HStack } from "@chakra-ui/react";
import { InfraObject, InfraStatus, InfraCategory, ObjectFilterRequest } from "../../type/models";
import { formatDate, getStatusColor } from "../../type/utils";
import React, { useState, useEffect, useRef } from "react";
import ObjectDetailsModal from "./ObjectDetailsModal";
import { Edit, Trash, X } from "react-feather";
import { useDeleteFakeEventMutation, useDeleteInfrastructureObjectMutation, useUpdateInfrastructureObjectMutation } from "../../redux/service/infrastructure";
import EditObjectModal from "./EditObjectModal";

interface ObjectTableProps {
  objects: InfraObject[];
  filterCriteria: ObjectFilterRequest;
  onFilterChange: (filterCriteria: ObjectFilterRequest) => void;
  onClear: () => void;
}

const ObjectTable: React.FC<ObjectTableProps> = ({ objects, onFilterChange, filterCriteria, onClear }) => {
  const [deleteObject] = useDeleteInfrastructureObjectMutation();
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { isOpen: isEditOpen, onOpen: onOpenEdit, onClose: onCloseEdit } = useDisclosure();
  const { isOpen: isDeleteOpen, onOpen: onOpenDelete, onClose: onCloseDelete } = useDisclosure();
  const [selectedObject, setSelectedObject] = useState<InfraObject | null>(null);
  const [objectToDelete, setObjectToDelete] = useState<InfraObject | null>(null);
  const [filteredObjects, setFilteredObjects] = useState<InfraObject[]>(objects);
  const cancelRef = useRef<HTMLButtonElement>(null);
  
  useEffect(() => {
    setFilteredObjects(objects);
  }, [objects]);

  const handleEditRow = (obj: InfraObject) => {
    setSelectedObject(obj);
    onOpenEdit();
  };

  const handleRowClick = (obj: InfraObject) => {
    setSelectedObject(obj);
    onOpen();
  };

  const handleDeleteClick = (obj: InfraObject, e: React.MouseEvent) => {
    e.stopPropagation();
    setObjectToDelete(obj);
    onOpenDelete();
  };

  const confirmDelete = () => {
    if (objectToDelete) {
      deleteObject(objectToDelete.id);
      setObjectToDelete(null);
      onCloseDelete();
    }
  };

  const handleFilterChange = (field: keyof typeof filterCriteria, value: any) => {
    onFilterChange({...filterCriteria, [field]: value, page: 0});
  };

  // Function to handle date change and format to 'YYYY-MM-DD' (no time)
  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const dateValue = e.target.value;
    if (dateValue) {
      const formattedDate = dateValue.split('T')[0]; // Lấy phần ngày trước dấu 'T'
      handleFilterChange("dateCaptured", formattedDate); // Gửi lại giá trị đã xử lý
    } else {
      handleFilterChange("dateCaptured", ""); // Clear filter nếu không có ngày
    }
  };

  return (
    <Box borderWidth="1px" borderRadius="lg" shadow="md" height="full" overflowY="auto">
      <Box>
        <Table
          variant="simple"
          style={{ tableLayout: "fixed" }}
          sx={{
            borderCollapse: "separate",
            borderSpacing: "0",
            "& th, & td": {
              borderWidth: "1px",
              borderColor: "gray.200",
              padding: "20px",
            },
          }}
          overflowX={"auto"}
        >
          <Thead bg="gray.100" position="sticky" top="0" zIndex={1}>
            <Tr>
              <Th width="15%" textAlign="center">
                Id
                <Input
                  size="sm"
                  placeholder="Filter by keyId..."
                  value={filterCriteria.keyId || ""}
                  onChange={(e) => handleFilterChange("keyId", e.target.value)}
                  mt={2}
                  bg="white"
                  borderRadius="md"
                />
              </Th>
              <Th width="15%" textAlign="center">
                Name
                <Input
                  size="sm"
                  placeholder="Filter name..."
                  value={filterCriteria.name || ""}
                  onChange={(e) => handleFilterChange("name", e.target.value)}
                  mt={2}
                  bg="white"
                  borderRadius="md"
                />
              </Th>
              <Th width="15%" textAlign="center">
                Category
                <Select
                  size="sm"
                  placeholder="All"
                  value={filterCriteria.category || ""}
                  onChange={(e) => handleFilterChange("category", e.target.value)}
                  mt={2}
                  bg="white"
                  borderRadius="md"
                >
                  {Object.values(InfraCategory).map((category) => (
                    <option key={category} value={category}>
                      {category}
                    </option>
                  ))}
                </Select>
              </Th>
              <Th width="20%" textAlign="center">
                Last Updated
                <Input
                  size="sm"
                  placeholder="Filter date..."
                  value={filterCriteria.dateCaptured || ""}
                  onChange={handleDateChange} // Sử dụng hàm xử lý mới
                  mt={2}
                  bg="white"
                  borderRadius="md"
                  type="date" // Loại input chỉ chứa ngày
                />
              </Th>
              <Th width="40%" textAlign="center">
                Location
                <Input
                  size="sm"
                  placeholder="Filter location..."
                  value={filterCriteria.location || ""}
                  onChange={(e) => handleFilterChange("location", e.target.value)}
                  mt={2}
                  bg="white"
                  borderRadius="md"
                />
              </Th>
              <Th width="10%" textAlign="center">
                Status
                <Select
                  size="sm"
                  placeholder="All"
                  value={filterCriteria.status || ""}
                  onChange={(e) => handleFilterChange("status", e.target.value)}
                  mt={2}
                  bg="white"
                  borderRadius="md"
                >
                  {Object.values(InfraStatus).map((status) => (
                    <option key={status} value={status}>
                      {status}
                    </option>
                  ))}
                </Select>
              </Th>
              <Th width="15%" textAlign="center">
                Actions
              </Th>
            </Tr>
          </Thead>
          <Tbody>
            {filteredObjects.length > 0 ? (
              filteredObjects.map((obj) => (
                <Tr
                  key={obj.id}
                  onClick={() => handleRowClick(obj)}
                  cursor="pointer"
                  _hover={{ bg: "gray.50" }}
                  transition="background-color 0.2s"
                > 
                  <Td width="250px" textAlign="center">
                    <Box as="span" display="block" overflow="hidden" textOverflow="ellipsis" whiteSpace="nowrap">
                      {obj?.info?.keyId}
                    </Box>
                  </Td>
                  <Td width="250px" textAlign="center">
                    <Box as="span" display="block" overflow="hidden" textOverflow="ellipsis" whiteSpace="nowrap">
                      {obj.name}
                    </Box>
                  </Td>
                  <Td width="250px" textAlign="center">
                    <Box as="span" display="block" overflow="hidden" textOverflow="ellipsis" whiteSpace="nowrap">
                      {obj.category}
                    </Box>
                  </Td>
                  <Td width="280px" textAlign="center">
                    <Box as="span" display="block" overflow="hidden" textOverflow="ellipsis" whiteSpace="nowrap">
                      {formatDate(obj.dateCaptured)}
                    </Box>
                  </Td>
                  <Td width="280px" textAlign="center">
                    <Box as="span" display="block" overflow="hidden" textOverflow="ellipsis" whiteSpace="nowrap">
                      {obj.location}
                    </Box>
                  </Td>
                  <Td width="250px" textAlign="center">
                    <Badge colorScheme={getStatusColor(obj.status)}>
                      {obj.status}
                    </Badge>
                  </Td>
                  <Td width="150px" textAlign="center" onClick={(e) => e.stopPropagation()}>
                    <Flex gap={6} alignContent="center">
                      <Button
                        size="sm"
                        colorScheme="blue"
                        variant="ghost"
                        onClick={() => handleEditRow(obj)}
                        leftIcon={<Edit size={16} />}
                      >
                        Edit
                      </Button>
                      <Button
                        size="sm"
                        colorScheme="red"
                        variant="ghost"
                        onClick={(e) => handleDeleteClick(obj, e)}
                        leftIcon={<Trash size={16} />}
                      >
                        Delete
                      </Button>
                    </Flex>
                  </Td>
                </Tr>
              ))
            ) : (
              <Tr>
                <Td colSpan={6} textAlign="center" py={4}>
                  No matching objects found
                </Td>
              </Tr>
            )}
          </Tbody>
        </Table>
      </Box>

      {/* Object Details Modal */}
      {selectedObject && (
        <ObjectDetailsModal
          isOpen={isOpen}
          onClose={onClose}
          obj={selectedObject}
        />
      )}

      {/* Edit Object Modal */}
      {selectedObject && (
        <EditObjectModal
          isOpen={isEditOpen}
          onClose={onCloseEdit}
          objectId={selectedObject?.id}
        />
      )}

      {/* Delete Confirmation Dialog */}
      <AlertDialog
        isOpen={isDeleteOpen}
        leastDestructiveRef={cancelRef}
        onClose={onCloseDelete}
      >
        <AlertDialogOverlay>
          <AlertDialogContent>
            <AlertDialogHeader fontSize="lg" fontWeight="bold">
              Delete Infrastructure Object
            </AlertDialogHeader>

            <AlertDialogBody>
              Are you sure you want to delete "{objectToDelete?.name}"? This action cannot be undone.
            </AlertDialogBody>

            <AlertDialogFooter>
              <Button ref={cancelRef} onClick={onCloseDelete}>
                Cancel
              </Button>
              <Button colorScheme="red" onClick={confirmDelete} ml={3}>
                Delete
              </Button>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialogOverlay>
      </AlertDialog>
    </Box>
  );
};

export default ObjectTable;
