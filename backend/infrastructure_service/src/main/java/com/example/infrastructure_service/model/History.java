package com.example.infrastructure_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.value.qual.ArrayLen;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDateTime dateCaptured;
  private String status;
  private double confidence;
  private Integer level;
  private String scheduleId;
  private String bbox;
  private Double realWidth;
  private Double realHeight;

  @ManyToOne
  private InfraImage image;

  @ManyToOne
  @JsonIgnore
  private InfraObject infraObject;
}
