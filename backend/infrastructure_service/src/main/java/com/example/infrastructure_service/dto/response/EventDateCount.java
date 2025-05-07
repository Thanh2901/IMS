package com.example.infrastructure_service.dto.response;

import java.sql.Date;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventDateCount {

  private Date dateCaptured;
  private long count;

}