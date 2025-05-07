package com.example.notification_service.client;

import com.example.notification_service.dto.response.CameraUserResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "camera-service")
public interface CameraClient {
  @GetMapping("/api/cameras/get_user/{camId}")
  ResponseEntity<List<CameraUserResponse>> getUserByCamId(@PathVariable String camId);
}
