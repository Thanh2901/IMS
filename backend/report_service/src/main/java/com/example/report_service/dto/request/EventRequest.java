package com.example.report_service.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class EventRequest {
  private List<String> cameraId;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String type;
}
