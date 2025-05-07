package com.example.infrastructure_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CategoryStatusCount {
  private String category;
  private String status;
  private long count;
}
