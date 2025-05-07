package com.example.infrastructure_service.dto.request;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class InfraReportRequest {
  private List<String> infraIds;
  private LocalDate startTime;
  private LocalDate endTime;
}
