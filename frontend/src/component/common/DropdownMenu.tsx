import { Box, Flex, Button, useOutsideClick } from "@chakra-ui/react";
import { useRef } from "react";
import useAuth from "../../hook/useAuth";
import { useNavigate } from "react-router-dom";
import { Settings, LogOut } from "react-feather";

interface DropdownMenuProps {
  onClose: () => void;
}

const DropdownMenu: React.FC<DropdownMenuProps> = ({ onClose }) => {
  const { logoutFunc } = useAuth();
  const navigate = useNavigate();
  const ref = useRef<HTMLDivElement>(null);

  useOutsideClick({
    ref: ref,
    handler: onClose,
  });

  const handleClickSettings = () => {
    navigate("/settings");
    onClose();
  };

  const handleClickLogout = () => {
    logoutFunc();
    onClose();
  };

  return (
    <Box
      ref={ref}
      position="absolute"
      top={10}
      right={1}
      mt={2}
      bg="white"
      borderRadius="md"
      boxShadow="lg"
      zIndex={1000}
      minW="150px"
    >
      <Flex direction="column" p={1}>
        <Button
          variant="ghost"
          justifyContent="start"
          leftIcon={<Settings size={16} />}
          fontSize="sm"
          px={3}
          _hover={{ bg: "gray.100" }}
          onClick={handleClickSettings}
        >
          Settings
        </Button>

        <Button
          variant="ghost"
          justifyContent="start"
          leftIcon={<LogOut size={16} />}
          fontSize="sm"
          px={3}
          _hover={{ bg: "gray.100" }}
          onClick={handleClickLogout}
        >
          Logout
        </Button>
      </Flex>
    </Box>
  );
};

export default DropdownMenu;