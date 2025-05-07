package com.example.infrastructure_service.dto.request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EventFilterRequest {
  private String startTime;
  private String endTime;
  private String eventStatus;
  private String status;
  private String category;
  private String name;
  private String cameraId;
  private String location;
  private String keyword;
  private Double confidence;
  private Integer level;

  private int page = 0;
  private int size = 10;
}
