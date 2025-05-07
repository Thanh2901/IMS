package com.example.notification_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDB {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String body;

  private String type;

  private Boolean isRead = false;

  private LocalDateTime createdAt;

  private LocalDateTime readAt;

  @Column(columnDefinition = "TEXT")
  private String additionalData;

  @ManyToMany(mappedBy = "notifications")
  @JsonIgnore
  private List<Device> devices = new ArrayList<>();
}
