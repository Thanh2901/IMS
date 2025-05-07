package com.example.infrastructure_service.dto.request;

import lombok.Data;

@Data
public class EndProcessRequest {
  private String scheduleId;
  private String videoUrl;
}
