package com.example.infrastructure_service.service;

import com.example.infrastructure_service.dto.EventNotificationDTO;
import com.example.infrastructure_service.dto.NotificationDTO;
import com.example.infrastructure_service.dto.SchedulingDTO;
import com.example.infrastructure_service.kafka.MessageProducer;
import com.example.infrastructure_service.mapper.EventMapper;
import com.example.infrastructure_service.model.Event;
import com.example.infrastructure_service.enums.EventStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final ObjectMapper objectMapper;
  private final MessageProducer messageProducer;
  private final EventMapper eventMapper;

  public void createEventNotification(Event event, EventStatus status) {
    try{

      EventNotificationDTO eventNotificationDTO = eventMapper.toEventNotificationDTO(event);
      String data = objectMapper.writeValueAsString(eventNotificationDTO);

      NotificationDTO notificationDTO = NotificationDTO.builder()
          .subject("Alarm Notification with type " + status)
          .content("Alarm Notification with " + event.getInfraObject().getName() + " at " + event.getDateCaptured())
          .data(data)
          .build();

      messageProducer.sendEventToNotification(objectMapper.writeValueAsString(notificationDTO));
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public void createScheduleNotification(SchedulingDTO schedulingDTO){
    try{
      String data = objectMapper.writeValueAsString(schedulingDTO);

      NotificationDTO notificationDTO = NotificationDTO.builder()
          .subject("Schedule Notification")
          .content("Schedule Notification with at " + schedulingDTO.getStartTime()+ " and end at " + schedulingDTO.getEndTime())
          .data(data)
          .build();

      messageProducer.sendEventToNotification(objectMapper.writeValueAsString(notificationDTO));
    }catch (Exception e){
      e.printStackTrace();
    }
  }


}
