package com.example.infrastructure_service.dto.request;

import lombok.Data;
import lombok.Getter;

@Data
public class DataInfraRequest {
  private String infraId;
  private String additionalData;
}
