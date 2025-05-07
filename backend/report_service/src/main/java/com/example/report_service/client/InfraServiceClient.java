package com.example.report_service.client;


import com.example.report_service.dto.ApiResponse;
import com.example.report_service.dto.request.CameraReportRequest;
import com.example.report_service.dto.response.ReportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

// send request to infra service for get data event
@FeignClient(name = "infrastructure-service")
public interface InfraServiceClient {
  @GetMapping("/api/infrastructures/report/infra/{infraId}")
  ApiResponse<ReportResponse> getHistoryInfraReport(@PathVariable String infraId);

  @PostMapping("/api/infrastructures/report/camera")
  ApiResponse<ReportResponse> getInfraReport(@RequestBody CameraReportRequest request, @RequestHeader("X-Credential-Id") String credentialId);

}
