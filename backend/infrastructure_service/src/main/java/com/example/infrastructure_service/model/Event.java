package com.example.infrastructure_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
  // Stores object events, state and time
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne
  private InfraObject infraObject;

  private LocalDateTime dateCaptured;

  private String status;

  private LocalDateTime endTime;

  private Integer level;
  private double confidence;

  private String eventStatus;

  private String scheduleId;

  private String description;

  private Boolean verified;
  private String bbox;
  private Double realWidth;
  private Double realHeight;

  @ManyToOne
  private InfraImage image;

}
