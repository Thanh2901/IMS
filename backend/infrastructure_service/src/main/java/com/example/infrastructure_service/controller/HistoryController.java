package com.example.infrastructure_service.controller;

import com.example.infrastructure_service.dto.ApiResponse;
import com.example.infrastructure_service.dto.request.CameraReportRequest;
import com.example.infrastructure_service.model.History;
import com.example.infrastructure_service.service.HistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/infrastructures/history")
@RequiredArgsConstructor
public class HistoryController {

  private final HistoryService historyService;

  @PostMapping("/camera")
  public ApiResponse<List<History>> getHistoriesByCameraAndDate(@RequestBody CameraReportRequest request) {
    return ApiResponse.<List<History>>builder()
        .message("Get list history by camera and date successfully")
        .data(historyService.getHistoriesByCamAndDate(request.getCameraId(), request.getStartTime(), request.getEndTime()))
        .build();
  }
  @GetMapping("/infra/{infraId}")
  public ApiResponse<List<History>> getHistoriesByInfra(@PathVariable String infraId) {
    return ApiResponse.<List<History>>builder()
        .message("Get list history by infra successfully")
        .data(historyService.getHistoriesByInfra(infraId))
        .build();
  }
  @GetMapping("/schedule/{scheduleId}")
  public ApiResponse<List<History>> getHistoriesBySchedule(@PathVariable String scheduleId) {
    return ApiResponse.<List<History>>builder()
        .message("Get list history by schedule successfully")
        .data(historyService.getHistoriesBySchedule(scheduleId))
        .build();
  }

}
