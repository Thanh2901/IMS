package com.example.infrastructure_service.dto.response;

import com.example.infrastructure_service.dto.EventDTO;
import com.example.infrastructure_service.model.Event;
import com.example.infrastructure_service.model.History;
import com.example.infrastructure_service.model.InfraObject;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class EventReportResponse {
  private String cameraId;
  private List<ObjectEvent> objects;

  @Data
  @AllArgsConstructor
  public static class ObjectEvent {
    private InfraObject infraObject;
    private List<EventDTO> events;
  }
}
