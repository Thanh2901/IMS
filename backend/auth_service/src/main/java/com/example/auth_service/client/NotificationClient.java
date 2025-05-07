package com.example.auth_service.client;

import com.example.auth_service.dto.request.SaveFcmTokenRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationClient {
  @PostMapping("/api/notifications/save-token")
  ResponseEntity<Void> saveToken(@RequestBody SaveFcmTokenRequest request);
}
