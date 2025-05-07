package com.example.infrastructure_service.kafka;

import com.example.infrastructure_service.model.InfraObjectProcess;
import com.example.infrastructure_service.model.JsonObject;
import com.example.infrastructure_service.service.JsonToInfraObjectProcess;
import com.example.infrastructure_service.service.InfraService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

  private final JsonToInfraObjectProcess infraObjectService;
  private final InfraService infraService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "service", groupId = "infra_service", containerFactory = "kafkaListenerContainerFactory", concurrency = "5")
  public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {

    try {
      log.info("Received record:");
      processMessage(record.value());

      acknowledgment.acknowledge();

    } catch (JsonProcessingException e) {
      log.error("JSON processing error for message: ", e);
      // Handle JSON parsing errors (e.g., send to a dead-letter topic)
    } catch (Exception e) {
      log.error("Unexpected error occurred while processing Kafka message:", e);
      // Handle unexpected errors (e.g., retry or send to a dead-letter topic)
    }

  }

  private void processMessage(String data) throws JsonProcessingException {
    // Parse JSON to JsonObject
    JsonObject jsonObject = objectMapper.readValue(data, JsonObject.class);
    log.debug("Parsed JSON object: {}", jsonObject);

    // Parse JsonObject to InfraObject list
    List<InfraObjectProcess> infraObjects = infraObjectService.processParseObject(jsonObject);
    log.info("Parsed infra objects: {}", infraObjects);

//    // Save or update infra objects in the database
//    infraService.processUpdateInfraList(infraObjects);
//    log.info("Successfully processed {} infra objects", infraObjects.size());
  }
}
