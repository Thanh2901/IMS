package com.example.infrastructure_service.dto.request;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;

@Data
public class NewInfraRequest {
  private String cameraId;
  private Double longitude;
  private Double latitude;
  private String category;
  private String name;
  private String status;
  private String additional;
  private String manageUnit;
}
