package com.example.infrastructure_service.controller;

import com.example.infrastructure_service.dto.ApiResponse;
import com.example.infrastructure_service.dto.response.StatisticsResponse;
import com.example.infrastructure_service.service.StatisticsService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/infrastructures/statistics")
@RequiredArgsConstructor
public class StatisticsController {

  private final StatisticsService statisticsService;

  @GetMapping
  public ApiResponse<StatisticsResponse> getStatistics(
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate endDate,
      @RequestParam String cameraId) {

    StatisticsResponse response = statisticsService.getStatistics(startDate, endDate, cameraId);
    return ApiResponse.<StatisticsResponse>builder()
        .message("Get statistics data successfully with camera id " + cameraId)
        .data(response)
        .build();
  }
}
