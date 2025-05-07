package com.example.infrastructure_service.controller;

import com.example.infrastructure_service.dto.ApiResponse;
import com.example.infrastructure_service.dto.request.CameraReportRequest;
import com.example.infrastructure_service.dto.response.EventReportResponse;
import com.example.infrastructure_service.dto.response.ReportResponse;
import com.example.infrastructure_service.service.ReportService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/infrastructures/report")
@RequiredArgsConstructor
public class ReportController {
  private final ReportService reportService;

  @GetMapping("/infra/{infraId}")
  public ApiResponse<ReportResponse> getHistoryInfraReport(@PathVariable String infraId) {
    return ApiResponse.<ReportResponse>builder()
        .message("get history infra report successfully")
        .data(reportService.getInfraReportData(infraId))
        .build();
  }

  @PostMapping("/camera")
  public ApiResponse<ReportResponse> getInfraReport(@RequestBody CameraReportRequest request, @RequestHeader("X-Credential-Id") String credentialId) {
    return ApiResponse.<ReportResponse>builder()
        .message("get history data for camera report successfully")
        .data(reportService.getCameraReportData(credentialId, request.getCameraId(), request.getStartTime(), request.getEndTime()))
        .build();
  }

}
