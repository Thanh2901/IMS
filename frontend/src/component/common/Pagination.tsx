import { Button, HStack, Text } from "@chakra-ui/react";
import { ChevronLeft, ChevronRight } from "react-feather";
import { getVisiblePages } from "../../type/utils";

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  onPageChange,
}) => {
  const pages = getVisiblePages(currentPage, totalPages);

  return (
    <HStack spacing={2} justify="center">
      <Button
        isDisabled={currentPage === 0}
        onClick={() => onPageChange(currentPage - 1)}
        leftIcon={<ChevronLeft size={18} />}
        variant="ghost"
        colorScheme="gray"
        size="sm"
        aria-label="Previous page"
      />

      {pages.map((page, index) =>
        typeof page === "string" ? (
          <Text key={`${page}-${index}`} color="gray.500" mx={1}>
            ...
          </Text>
        ) : (
          <Button
            key={`page-${page}`}
            onClick={() => onPageChange(page)}
            variant={currentPage === page ? "solid" : "ghost"}
            colorScheme={currentPage === page ? "blue" : "gray"}
            size="sm"
            borderRadius="md"
            minW="8"
          >
            {page + 1}
          </Button>
        )
      )}

      <Button
        isDisabled={currentPage === totalPages - 1}
        onClick={() => onPageChange(currentPage + 1)}
        rightIcon={<ChevronRight size={18} />}
        variant="ghost"
        colorScheme="gray"
        size="sm"
        aria-label="Next page"
      />
    </HStack>
  );
};

export default Pagination;
