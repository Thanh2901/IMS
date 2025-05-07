package com.example.notification_service.controller;

import com.example.notification_service.dto.ApiResponse;
import com.example.notification_service.dto.request.SaveFcmTokenRequest;
import com.example.notification_service.model.Notice;
import com.example.notification_service.model.NotificationDB;
import com.example.notification_service.service.DeviceService;
import com.example.notification_service.service.NotificationService;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
  public final DeviceService deviceService;
  public final NotificationService notificationService;

  @PostMapping("/save-token")
  public ApiResponse<Void> saveToken(@RequestBody SaveFcmTokenRequest request) {
    if(request.getFcmToken()!=null && !request.getFcmToken().isEmpty()) {
      deviceService.saveOrUpdateFCMToken(request.getUserId(), request.getFcmToken());
    }
    return ApiResponse.<Void>builder()
        .message("Save token successfully")
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<NotificationDB> getNotification(@PathVariable("id") Long id) {
    return ApiResponse.<NotificationDB>builder()
            .message("Get notification by id")
            .data(notificationService.getNotification(id))
            .build();
  }

  @GetMapping("")
  public ApiResponse<List<NotificationDB>> getNotifications(@RequestHeader("X-Credential-Id") String credentialId, @RequestParam(required = false) Boolean isRead) {
    return ApiResponse.<List<NotificationDB>>builder()
        .message("Get notification by id")
        .data(notificationService.getNotifications(credentialId, isRead))
        .build();
  }

  @PatchMapping("/read/{id}")
  public ApiResponse<NotificationDB> readNotification(@PathVariable Long id){
    return ApiResponse.<NotificationDB>builder()
        .message("Read notification successfully")
        .data(notificationService.readNotification(id))
        .build();
  }

  @PatchMapping("/read/all")
  public ApiResponse<Void> readAllNotification(@RequestHeader("X-Credential-Id") String credentialId){
    notificationService.readAllNotification(credentialId);
    return ApiResponse.<Void>builder()
        .message("Read all notification successfully")
        .build();
  }
}
