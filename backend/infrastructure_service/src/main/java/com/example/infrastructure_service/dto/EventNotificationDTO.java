package com.example.infrastructure_service.dto;

import lombok.Data;

@Data
public class EventNotificationDTO {
  private String type;
  private String cameraId;
  private String eventId;
  private String time;
  private String status;
  private String urlImage;
  private String category;
  private String name;
  private String level;
  private String confidence;
  private String longitude;
  private String latitude;
  private String location;
}
