package com.example.infrastructure_service.dto;

import com.example.infrastructure_service.model.Event;
import com.example.infrastructure_service.model.InfraImage;
import java.time.LocalDateTime;

public record EventDTO(
    String id,
    LocalDateTime dateCaptured,
    String status,
    LocalDateTime endTime,
    Integer level,
    double confidence,
    String eventStatus,
    InfraImage image
) {
  public EventDTO(Event event) {
    this(event.getId(), event.getDateCaptured(), event.getStatus(),
        event.getEndTime(), event.getLevel(), event.getConfidence(),
        event.getEventStatus(), event.getImage());
  }
}

