package com.example.infrastructure_service.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponse {
  private List<EventDateCount> eventStatistics;
  private List<CategoryStatusCount> objectStatistics;
}
