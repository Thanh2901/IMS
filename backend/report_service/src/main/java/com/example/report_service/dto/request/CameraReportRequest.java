package com.example.report_service.dto.request;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class CameraReportRequest {
  private String cameraId;
  private LocalDate startTime;
  private LocalDate endTime;
}
