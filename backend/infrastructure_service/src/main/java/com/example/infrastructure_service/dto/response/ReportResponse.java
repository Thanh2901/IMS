package com.example.infrastructure_service.dto.response;

import com.example.infrastructure_service.dto.EventDTO;
import com.example.infrastructure_service.model.History;
import com.example.infrastructure_service.model.InfraObject;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class ReportResponse {
  private String cameraId;
  private List<ObjectHistory> objects;

  @Data
  @AllArgsConstructor
  public static class ObjectHistory {
    private InfraObject infraObject;
    private List<History> histories;
    private List<EventDTO> events;
  }

}
