package com.example.notification_service.kafka;

import com.example.notification_service.model.Notice;
import com.example.notification_service.model.NotificationDB;
import com.example.notification_service.service.DeviceService;
import com.example.notification_service.service.NoticeService;
import com.example.notification_service.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

  private final NotificationService notificationService;
  private final NoticeService noticeService;

  @KafkaListener(topics = "notification", groupId = "notification_service", containerFactory = "kafkaListenerContainerFactory", concurrency = "5")
  public void listen(String data) {
    try{

      Notice notice = noticeService.createNotice(data);

      notificationService.sendMulticastNotification(notice);

      notificationService.saveNotification(notice);

      log.info("Notification saved {}", notice.getContent());

    }catch (Exception e){
      throw new RuntimeException(e);
    }

  }
}
