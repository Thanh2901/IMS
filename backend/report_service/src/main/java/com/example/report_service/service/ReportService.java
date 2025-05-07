package com.example.report_service.service;

import com.example.report_service.client.InfraServiceClient;
import com.example.report_service.dto.ApiResponse;
import com.example.report_service.dto.request.CameraReportRequest;
import com.example.report_service.dto.response.ReportResponse;
import java.io.IOException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final InfraServiceClient infraServiceClient;

  public byte[] getHistoryReport(String infraId) throws Exception {
    ApiResponse<ReportResponse> apiResponse = infraServiceClient.getHistoryInfraReport(infraId);

    ReportResponse reportData = apiResponse.getData();
    System.out.println(reportData.getCameraId());
    ExcelReportService excelReportService = new ExcelReportService(reportData);

    return  excelReportService.generateComprehensiveReport();
  }

  public byte[] getCameraReport(CameraReportRequest request, String userId) throws Exception {
    ApiResponse<ReportResponse> apiResponse = infraServiceClient.getInfraReport(request, userId);
    ReportResponse reportData = apiResponse.getData();
    System.out.println(reportData.getCameraId());
    ExcelReportService excelReportService = new ExcelReportService(reportData);

    return  excelReportService.generateComprehensiveReport();
  }

}
