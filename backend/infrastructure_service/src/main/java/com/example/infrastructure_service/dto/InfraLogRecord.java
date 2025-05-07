package com.example.infrastructure_service.dto;

import com.example.infrastructure_service.model.InfraObject;
import com.example.infrastructure_service.model.InfraObjectProcess;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfraLogRecord {
  private InfraObject oldInfraObject;
  private InfraObjectProcess newInfraObject;
  private Double distance;
}
