package com.example.infrastructure_service.dto.request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CheckLostRequest {
  private String cameraId;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
}
