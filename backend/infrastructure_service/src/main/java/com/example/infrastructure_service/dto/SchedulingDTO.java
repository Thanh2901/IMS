package com.example.infrastructure_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SchedulingDTO {
  private String id;
  private String startTime;
  private String endTime;
  private String cameraId;
  private String videoUrl;
  private String gpsLogsUrl;
}

