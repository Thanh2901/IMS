package com.example.infrastructure_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO {
  private String subject;
  private String content;
  private String data;
  private String userId;
}
