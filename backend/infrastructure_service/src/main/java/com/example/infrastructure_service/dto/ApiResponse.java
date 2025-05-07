package com.example.infrastructure_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ApiResponse<T> {

  @Builder.Default
  private int code = 1000;
  private String message;
  private T data;
}
