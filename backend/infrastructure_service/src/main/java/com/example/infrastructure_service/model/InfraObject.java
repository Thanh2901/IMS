package com.example.infrastructure_service.model;

import com.example.infrastructure_service.enums.InfraType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "infra_objects")
public class InfraObject {
  // stores a unique object identified by cameraId
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
  private double confidence;
  private Integer level;
  private String location;
  private String scheduleId;
  private String bbox;
  private Boolean isUpdated = true;
  private Double realWidth;
  private Double realHeight;

  @Enumerated(EnumType.STRING)
  private InfraType type;

  @OneToOne(cascade = CascadeType.ALL)
  private InfraInfo info;

  @ManyToOne
  @JoinColumn(name = "image_id")
  private InfraImage image;

  @OneToMany(mappedBy = "infraObject", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Event> events;

  @OneToMany(mappedBy = "infraObject", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<History> histories;
}
