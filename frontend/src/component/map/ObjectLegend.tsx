import { Flex, Box, Text, Image } from "@chakra-ui/react";
import roadIcon from "../../assets/road.png";
import signIcon from "../../assets/sign.png";
import guardrailIcon from "../../assets/guardrail.png";
import lampIcon from "../../assets/lamp.png";

const objectIcons = {
  SIGN: signIcon,
  ROAD: roadIcon,
  LAMP: lampIcon,
  GUARDRAIL: guardrailIcon
};

const Legend = () => {
  return (
    <Flex justifyContent="center" alignItems="center" gap={4}>
      {Object.entries(objectIcons).map(([key, icon]) => (
        <Flex key={key} alignItems="center" gap={2}>
          <Image src={icon} alt={key} boxSize="20px" />
          <Text fontSize="sm" fontWeight="medium">{key}</Text>
        </Flex>
      ))}
    </Flex>
  );
};

export default Legend;