package com.example.infrastructure_service.model;

import com.example.infrastructure_service.enums.EventStatus;
import com.example.infrastructure_service.enums.InfraType;
import com.example.infrastructure_service.enums.ProcessStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfraObjectProcess {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String cameraId;
  private LocalDateTime dateCaptured;
  private Double longitude;
  private Double latitude;
  private String category;
  private String name;
  private String status;
  private Double confidence;
  private Integer level;
  private String location;
  private String scheduleId;
  private String bbox;
  private Double realWidth;
  private Double realHeight;

  @Enumerated(EnumType.STRING)
  private EventStatus eventStatus;

  @Enumerated(EnumType.STRING)
  private InfraType type;


  @ManyToOne
  private InfraImage image;

  @Enumerated(EnumType.STRING)
  private ProcessStatus processStatus;

  @ManyToOne
  private InfraObject infraObject;
}
