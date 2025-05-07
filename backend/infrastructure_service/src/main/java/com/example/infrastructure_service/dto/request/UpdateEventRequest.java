package com.example.infrastructure_service.dto.request;

import com.example.infrastructure_service.enums.EventStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEventRequest {
  private String description;
  private Boolean verified;
}
