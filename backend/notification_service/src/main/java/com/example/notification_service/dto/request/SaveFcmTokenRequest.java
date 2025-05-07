package com.example.notification_service.dto.request;

import lombok.Data;

@Data
public class SaveFcmTokenRequest {
  String userId;
  String fcmToken;
}
