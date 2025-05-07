package com.example.auth_service.dto.request;

import lombok.Data;

@Data
public class SaveFcmTokenRequest {
  String userId;
  String fcmToken;
}
