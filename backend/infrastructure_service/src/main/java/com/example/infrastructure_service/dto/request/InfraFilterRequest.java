package com.example.infrastructure_service.dto.request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class InfraFilterRequest {
  private String name;
  private String location;
  private String dateCaptured;
  private String status;
  private String category;
  private String cameraId;
  private String keyword;
  private String type;
  private Boolean isPaged;

  private int page = 0;
  private int size = 10;
}
