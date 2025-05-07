import { useEffect, useState } from "react";
import { Search } from "react-feather";
import { Input, Box, Spinner, Flex, Button, IconButton } from "@chakra-ui/react";
import { useSearchObjectsQuery } from "../../redux/service/infrastructure";
import { InfraObject } from "../../type/models";
import SearchDropdown from "./SearchDropdown";

interface SearchBarProps {
  defaultObjects: InfraObject[];
  onObjectSelect: (object: InfraObject) => void;
  onSetObjects: (objects: InfraObject[]) => void;
}

const SearchBar: React.FC<SearchBarProps> = ({ onObjectSelect, onSetObjects, defaultObjects }) => {
  const [keyword, setKeyword] = useState<string>("");
  const [debouncedKeyword, setDebouncedKeyword] = useState<string>("");
  const [objects, setObjects] = useState<InfraObject[]>([]);
  const { data: searchedObjects, isLoading } = useSearchObjectsQuery(
    { params: { keyword: debouncedKeyword, page: 0, size: 10000 } }, // set size big to fetch all objects
    { skip: !debouncedKeyword }
  );

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedKeyword(keyword);
    }, 500);

    return () => clearTimeout(handler);
  }, [keyword]);

  useEffect(() => {
    if (searchedObjects) {
      setObjects(searchedObjects.pageData);
    }
    if (!debouncedKeyword) {
      setObjects([]);
      onSetObjects(defaultObjects);
    }
  }, [searchedObjects, debouncedKeyword]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setKeyword(e.target.value);
  };

  const handleSetObjects = () => {
    onSetObjects(objects);
    setObjects([]);
  };

  // useEffect(() => {
  //   const handleClickOutside = (event: MouseEvent) => {
  //     setKeyword("");
  //   };

  //   document.addEventListener("mousedown", handleClickOutside);
  //   return () => {
  //     document.removeEventListener("mousedown", handleClickOutside);
  //   };
  // }, []);

  return (
    <Box position="relative" w="full" maxW="500px">
      <Flex gap={2}>
        <Input
          placeholder="Search an object..."
          value={keyword}
          onChange={handleInputChange}
          bg="white"
          borderRadius="md"
        />
        <Button
          aria-label="search icon"
          //icon={<Search size={20} />}
          onClick={handleSetObjects}
          colorScheme="blue"
          disabled={!keyword}
        >
          Enter
        </Button>
      </Flex>

      {isLoading && <Spinner size="sm" position="absolute" right={2} top="50%" transform="translateY(-50%)" />}

      {objects && <SearchDropdown
        objects={objects.slice(0, 5)}
        onSelect={onObjectSelect}
        onSetObjects={onSetObjects}
        setObjects={setObjects} />}

    </Box>
  );
};

export default SearchBar;