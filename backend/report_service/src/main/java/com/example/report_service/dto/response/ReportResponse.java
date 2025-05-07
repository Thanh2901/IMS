package com.example.report_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import jdk.jfr.Event;
import lombok.AllArgsConstructor;
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
    private List<Event> events;
  }
  @Data
  public static class InfraObject {
    private String id;
    private String cameraId;
    private LocalDateTime dateCaptured;
    private Double longitude;
    private Double latitude;
    private String category;
    private String name;
    private String status;
    private double confidence;
    private Integer level;
    private String location;
    private String additional;
    private InfraImage image;
  }
  @Data
  public static class History {
    private Long id;
    private LocalDateTime dateCaptured;
    private String status;
    private double confidence;
    private Integer level;
    private InfraImage image;
  }

  @Data
  public static class Event {
    private String id;
    private LocalDateTime dateCaptured;
    private String status;
    private LocalDateTime endTime;
    private Integer level;
    private double confidence;
    private String eventStatus;
    private InfraImage image;
  }

  @Data
  public static class InfraImage {
    private Long id;
    private String pathUrl;
  }

}
