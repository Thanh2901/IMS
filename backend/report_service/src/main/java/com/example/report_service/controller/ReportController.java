package com.example.report_service.controller;

import com.example.report_service.dto.ApiResponse;
import com.example.report_service.dto.request.CameraReportRequest;
import com.example.report_service.dto.request.EventRequest;
import com.example.report_service.dto.response.ReportResponse;
import com.example.report_service.service.ReportService;
import java.io.IOException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @GetMapping("/infra/{infraId}")
  public ResponseEntity<ByteArrayResource> getHistoryReport(@PathVariable String infraId) {
    try {
      // Generate Excel file
      byte[] excelContent = reportService.getHistoryReport(infraId);

      // Create response with file download
      ByteArrayResource resource = new ByteArrayResource(excelContent);

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Camera_Report_" +
              System.currentTimeMillis() + ".xlsx")
          .contentLength(excelContent.length)
          .body(resource);

    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @PostMapping ("/camera")
  public ResponseEntity<ByteArrayResource> getCameraReport(@RequestBody CameraReportRequest request ,@RequestHeader("X-Credential-Id") String credentialId) {
    try {
      // Generate Excel file
      byte[] excelContent = reportService.getCameraReport(request, credentialId);

      // Create response with file download
      ByteArrayResource resource = new ByteArrayResource(excelContent);

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Camera_Report_" +
              System.currentTimeMillis() + ".xlsx")
          .contentLength(excelContent.length)
          .body(resource);

    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

}
