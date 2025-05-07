package com.example.infrastructure_service.dto.request;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class FindNearestInfraRequest {
  private String cameraId;
  private String name;
  private String category;
  private Double latitude;
  private Double longitude;
}
