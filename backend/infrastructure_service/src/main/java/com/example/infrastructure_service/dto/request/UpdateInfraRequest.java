package com.example.infrastructure_service.dto.request;

import lombok.Data;

@Data
public class UpdateInfraRequest {
  private String infraId;
  private Double longitude;
  private Double latitude;
  private String category;
  private String name;
  private String status;
  private String additional;
  private String manageUnit;
}
