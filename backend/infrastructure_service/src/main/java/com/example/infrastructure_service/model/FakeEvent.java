package com.example.infrastructure_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FakeEvent {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private double latitude;
  private double longitude;
  private String category;
  private String name;
  private LocalDateTime time;
  private double confidence;
  private String status;
  private String location;

  @ManyToOne
  private InfraImage image;
}
