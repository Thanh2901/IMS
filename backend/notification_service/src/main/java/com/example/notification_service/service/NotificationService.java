package com.example.notification_service.service;

import com.example.notification_service.model.Device;
import com.example.notification_service.model.Notice;
import com.example.notification_service.model.NotificationDB;
import com.example.notification_service.repository.DeviceRepository;
import com.example.notification_service.repository.NotificationRepository;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final DeviceRepository deviceRepository;
  private final NotificationRepository notificationRepository;


  public void sendMulticastNotification(Notice notice) throws FirebaseMessagingException {
    // Create the notification payload
    Notification notification = Notification.builder()
        .setTitle(notice.getSubject())
        .setBody(notice.getContent())
        .build();

    // Create the message
    MulticastMessage message = MulticastMessage.builder()
        .setNotification(notification)
        .addAllTokens(notice.getRegistrationTokens())
        .build();

    // Send the multicast message
    BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

    // Handle the response
    if (response.getFailureCount() > 0) {
      List<SendResponse> responses = response.getResponses();
      for (int i = 0; i < responses.size(); i++) {
        if (!responses.get(i).isSuccessful()) {
          System.err.println("Failed to send message to token: " + notice.getRegistrationTokens().get(i));
          System.err.println("Error: " + responses.get(i).getException().getMessage());
        }
      }
    }

    System.out.println("Successfully sent message to " + response.getSuccessCount() + " devices.");
  }

  public void saveNotification(Notice notice) {

    List<Device> devices = deviceRepository.findByFcmTokenIn(notice.getRegistrationTokens());


    NotificationDB notificationDB = new NotificationDB();
    notificationDB.setTitle(notice.getSubject());
    notificationDB.setBody(notice.getContent());
    notificationDB.setAdditionalData(notice.getData().toString());
    notificationDB.setIsRead(false);
    notificationDB.setCreatedAt(LocalDateTime.now());

    notificationDB.setDevices(devices);

    notificationRepository.save(notificationDB);

    for (Device device : devices) {
      device.getNotifications().add(notificationDB);
      deviceRepository.save(device);
    }
  }

  public NotificationDB getNotification(Long id) {
    // Device device = deviceRepository.findByUserId(userId).getFirst();
    return notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
  }

  public List<NotificationDB> getNotifications(String userId, Boolean isRead) {
    Device device = deviceRepository.findByUserId(userId).getFirst();

    return notificationRepository.getNotificationsByIdAndRead(device.getId(), isRead);
  }

  public NotificationDB readNotification(Long id) {
    NotificationDB notificationDB = notificationRepository.findById(id).orElse(null);

    if (notificationDB == null) {
      throw new RuntimeException("Notification not found");
    }

    notificationDB.setIsRead(true);
    notificationDB.setReadAt(LocalDateTime.now());

    return notificationRepository.save(notificationDB);
  }

  public void readAllNotification(String credentialId) {
    Device device = deviceRepository.findByUserId(credentialId).getFirst();

    List<NotificationDB> notifications = notificationRepository.findByDevicesIdAndIsRead(
        device.getId(), false);

    for (NotificationDB notification : notifications) {
      notification.setIsRead(true);
      notification.setReadAt(LocalDateTime.now());
    }

    notificationRepository.saveAll(notifications);
  }
}
