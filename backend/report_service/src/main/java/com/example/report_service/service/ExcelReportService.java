package com.example.report_service.service;

import com.example.report_service.dto.response.ReportResponse;
import com.example.report_service.dto.response.ReportResponse.Event;
import com.example.report_service.dto.response.ReportResponse.History;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReportService {

  private ReportResponse reportData;

  public ExcelReportService(ReportResponse reportData) {
    this.reportData = reportData;
  }

  public byte[] generateComprehensiveReport() throws Exception {
    XSSFWorkbook workbook = new XSSFWorkbook();

    // Styles
    XSSFCellStyle headerStyle = createHeaderStyle(workbook);
    XSSFCellStyle dataStyle = createDataStyle(workbook);
    XSSFCellStyle dateStyle = createDateStyle(workbook);
    XSSFCellStyle warningStyle = createWarningStyle(workbook);
    XSSFCellStyle eventStyle = createEventStyle(workbook);

    // Main Overview Sheet
    XSSFSheet overviewSheet = workbook.createSheet("Overview");
    createOverviewSheet(overviewSheet, headerStyle, dataStyle);

    // Detailed Object History Sheet
    XSSFSheet detailSheet = workbook.createSheet("Object Details");
    createDetailSheet(detailSheet, headerStyle, dataStyle, dateStyle);

    XSSFSheet historySheet = workbook.createSheet("Object Histories");
    createObjectHistoriesSheet(historySheet, headerStyle, dataStyle, dateStyle, warningStyle);

    // Create Event Analysis Sheet
    XSSFSheet eventSheet = workbook.createSheet("Event Analysis");
    createEventAnalysisSheet(eventSheet, headerStyle, dataStyle, dateStyle, eventStyle);

    // Create Event Status Sheet
    XSSFSheet eventStatusSheet = workbook.createSheet("Event Status");
    createEventStatusSheet(eventStatusSheet, headerStyle, dataStyle);

    createDailyHistoryStatusSheet(workbook, headerStyle, dataStyle);

    // Statistical Analysis Sheets
    createCategoryAnalysisSheet(workbook, headerStyle, dataStyle);
    createConfidenceAnalysisSheet(workbook, headerStyle, dataStyle);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    workbook.write(outputStream);
    return outputStream.toByteArray();
  }

  private void createOverviewSheet(XSSFSheet sheet, XSSFCellStyle headerStyle, XSSFCellStyle dataStyle) {
    // Summary statistics
    int rowNum = 0;
    Row titleRow = sheet.createRow(rowNum++);
    Cell titleCell = titleRow.createCell(0);
    titleCell.setCellValue("Infrastructure Object Tracking - Summary Report");
    titleCell.setCellStyle(headerStyle);
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

    // Thời gian tạo báo cáo
    Row dateRow = sheet.createRow(rowNum++);
    dateRow.createCell(0).setCellValue("Generated At:");
    dateRow.createCell(1).setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

    // Key Metrics
    String[] metrics = {
        "Total Unique Objects",
        "Total Cameras",
        "Total Events",
        "Objects by Category",
        "Average Confidence Level",
        "Event Success Rate"
    };

    Object[] values = calculateOverviewMetrics();

    for (int i = 0; i < metrics.length; i++) {
      Row row = sheet.createRow(rowNum++);
      Cell metricCell = row.createCell(0);
      metricCell.setCellValue(metrics[i]);
      metricCell.setCellStyle(headerStyle);

      Cell valueCell = row.createCell(1);
      valueCell.setCellValue(values[i].toString());
      valueCell.setCellStyle(dataStyle);
    }
  }

  private void createDetailSheet(XSSFSheet sheet, XSSFCellStyle headerStyle,
      XSSFCellStyle dataStyle, XSSFCellStyle dateStyle) {
    // Tạo tiêu đề cột
    String[] headers = {
        "Camera ID", "Object ID", "Name", "Category",
        "History Count", "Event Count", "History Time", "History Status", "Location",
        "Status", "Confidence", "Latitude", "Longitude", "Last Updated"
    };

    Row headerRow = sheet.createRow(0);
    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(headerStyle);
    }

    // Đổ dữ liệu
    int rowNum = 1;
    for (ReportResponse.ObjectHistory objHistory : reportData.getObjects()) {
      ReportResponse.InfraObject obj = objHistory.getInfraObject();
      List<History> histories = objHistory.getHistories();
      List<Event> events = objHistory.getEvents();

      if (histories.isEmpty()) {
        // Nếu không có history, vẫn tạo một dòng bình thường
        Row row = sheet.createRow(rowNum++);
        fillObjectData(row, obj, objHistory.getHistories().size(), events.size(), dateStyle);
      } else {
        // Nếu có history, mỗi history là một dòng riêng
        boolean firstRow = true;
        for (ReportResponse.History history : histories) {
          Row row = sheet.createRow(rowNum++);

          if (firstRow) { // Chỉ ghi thông tin Object vào dòng đầu tiên
            fillObjectData(row, obj, histories.size(), events.size(), dateStyle);
            firstRow = false;
          }

          // Ghi thông tin lịch sử vào cột riêng
          Cell historyTimeCell = row.createCell(6);
          historyTimeCell.setCellValue(history.getDateCaptured());
          historyTimeCell.setCellStyle(dateStyle);

          row.createCell(7).setCellValue(history.getStatus());
        }
      }
    }

    // Tự động chỉnh kích thước cột
    for (int i = 0; i < headers.length; i++) {
      sheet.autoSizeColumn(i);
    }
  }

  // Hàm hỗ trợ để điền thông tin Object vào dòng đầu tiên
  private void fillObjectData(Row row, ReportResponse.InfraObject obj, int historyCount, int eventCount, XSSFCellStyle dateStyle) {
    row.createCell(0).setCellValue(obj.getCameraId());
    row.createCell(1).setCellValue(obj.getId());
    row.createCell(2).setCellValue(obj.getName());
    row.createCell(3).setCellValue(obj.getCategory());
    row.createCell(9).setCellValue(obj.getStatus());
    row.createCell(10).setCellValue(obj.getConfidence());
    row.createCell(11).setCellValue(obj.getLatitude());
    row.createCell(12).setCellValue(obj.getLongitude());

    Cell dateCell = row.createCell(13);
    dateCell.setCellValue(obj.getDateCaptured());
    dateCell.setCellStyle(dateStyle);

    row.createCell(4).setCellValue(historyCount);
    row.createCell(5).setCellValue(eventCount);
    row.createCell(8).setCellValue(obj.getLocation());
  }

  private void createCategoryAnalysisSheet(XSSFWorkbook workbook,
      XSSFCellStyle headerStyle,
      XSSFCellStyle dataStyle) {
    XSSFSheet categorySheet = workbook.createSheet("Category Analysis");

    // Group objects by category
    Map<String, Long> categoryCount = reportData.getObjects().stream()
        .map(ReportResponse.ObjectHistory::getInfraObject)
        .collect(Collectors.groupingBy(
            ReportResponse.InfraObject::getCategory,
            Collectors.counting()
        ));

    // Create headers
    Row headerRow = categorySheet.createRow(0);
    String[] headers = {"Category", "Count", "Percentage"};
    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(headerStyle);
    }

    // Populate data
    long totalObjects = categoryCount.values().stream().mapToLong(Long::longValue).sum();
    int rowNum = 1;
    for (Map.Entry<String, Long> entry : categoryCount.entrySet()) {
      Row row = categorySheet.createRow(rowNum++);
      row.createCell(0).setCellValue(entry.getKey());
      row.createCell(1).setCellValue(entry.getValue());
      row.createCell(2).setCellValue(
          String.format("%.2f%%", (entry.getValue() * 100.0) / totalObjects)
      );
    }
  }

  private void createConfidenceAnalysisSheet(XSSFWorkbook workbook,
      XSSFCellStyle headerStyle,
      XSSFCellStyle dataStyle) {
    XSSFSheet confidenceSheet = workbook.createSheet("Confidence Analysis");

    // Confidence level ranges
    String[] confidenceLevels = {
        "0-20%", "20-40%", "40-60%", "60-80%", "80-100%"
    };
    int[] confidenceCounts = new int[5];

    // Count confidence levels per history entry
    long totalHistories = 0;
    for (ReportResponse.ObjectHistory objHistory : reportData.getObjects()) {
      for (ReportResponse.History history : objHistory.getHistories()) {
        double confidence = history.getConfidence()*100.0; // Lấy confidence từ từng history
        int index = (int) (confidence / 20);
        if (index == 5) index = 4;  // Handle 100%
        confidenceCounts[index]++;
        totalHistories++;
      }
    }

    // Create headers
    Row headerRow = confidenceSheet.createRow(0);
    String[] headers = {"Confidence Range", "Count", "Percentage"};
    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(headerStyle);
    }

    // Populate data
    for (int i = 0; i < confidenceLevels.length; i++) {
      Row row = confidenceSheet.createRow(i + 1);
      row.createCell(0).setCellValue(confidenceLevels[i]);
      row.createCell(1).setCellValue(confidenceCounts[i]);
      row.createCell(2).setCellValue(
          totalHistories > 0 ? String.format("%.2f%%", (confidenceCounts[i] * 100.0) / totalHistories) : "0.00%"
      );
    }
  }

  private Object[] calculateOverviewMetrics() {
    Set<String> uniqueObjects = reportData.getObjects().stream()
        .map(obj -> obj.getInfraObject().getId())
        .collect(Collectors.toSet());

    Set<String> uniqueCameras = reportData.getObjects().stream()
        .map(obj -> obj.getInfraObject().getCameraId())
        .collect(Collectors.toSet());

    // Count total events
    long totalEvents = reportData.getObjects().stream()
        .mapToLong(obj -> obj.getEvents().size())
        .sum();

    Map<String, Long> categoryCounts = reportData.getObjects().stream()
        .map(ReportResponse.ObjectHistory::getInfraObject)
        .collect(Collectors.groupingBy(
            ReportResponse.InfraObject::getCategory,
            Collectors.counting()
        ));

    double avgConfidence = reportData.getObjects().stream()
        .mapToDouble(obj -> obj.getInfraObject().getConfidence())
        .average()
        .orElse(0.0);

    // Calculate event success rate
    long successfulEvents = reportData.getObjects().stream()
        .flatMap(obj -> obj.getEvents().stream())
        .filter(event -> "SUCCESS".equals(event.getEventStatus()))
        .count();

    String eventSuccessRate = totalEvents > 0
        ? String.format("%.2f%%", (successfulEvents * 100.0) / totalEvents)
        : "N/A";

    return new Object[]{
        uniqueObjects.size(),
        uniqueCameras.size(),
        totalEvents,
        categoryCounts.toString(),
        String.format("%.2f%%", avgConfidence),
        eventSuccessRate
    };
  }

  // Utility style methods
  private XSSFCellStyle createHeaderStyle(XSSFWorkbook workbook) {
    XSSFCellStyle style = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setBold(true);
    font.setColor(IndexedColors.WHITE.getIndex());
    style.setFont(font);
    style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    style.setAlignment(HorizontalAlignment.CENTER);
    return style;
  }

  private XSSFCellStyle createDataStyle(XSSFWorkbook workbook) {
    XSSFCellStyle style = workbook.createCellStyle();
    style.setAlignment(HorizontalAlignment.LEFT);
    return style;
  }

  private XSSFCellStyle createDateStyle(XSSFWorkbook workbook) {
    XSSFCellStyle style = workbook.createCellStyle();
    CreationHelper createHelper = workbook.getCreationHelper();
    style.setDataFormat(
        createHelper.createDataFormat().getFormat("yyyy-mm-dd HH:mm:ss")
    );
    return style;
  }

  private XSSFCellStyle createWarningStyle(XSSFWorkbook workbook) {
    XSSFCellStyle style = workbook.createCellStyle();
    style.setFillForegroundColor(IndexedColors.CORAL.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    XSSFFont font = workbook.createFont();
    font.setColor(IndexedColors.WHITE.getIndex());
    style.setFont(font);
    return style;
  }

  private XSSFCellStyle createEventStyle(XSSFWorkbook workbook) {
    XSSFCellStyle style = workbook.createCellStyle();
    style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    return style;
  }

  private void createObjectHistoriesSheet(XSSFSheet sheet,
      XSSFCellStyle headerStyle,
      XSSFCellStyle dataStyle,
      XSSFCellStyle dateStyle,
      XSSFCellStyle warningStyle) {
    // Create headers
    String[] headers = {
        "Object ID", "History ID", "Status", "Confidence",
        "Level", "Date Captured", "Image Available"
    };

    Row headerRow = sheet.createRow(0);
    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(headerStyle);
    }

    // Populate history details
    int rowNum = 1;
    for (ReportResponse.ObjectHistory objHistory : reportData.getObjects()) {
      for (History history : objHistory.getHistories()) {
        Row row = sheet.createRow(rowNum++);

        // Object Identification
        row.createCell(0).setCellValue(objHistory.getInfraObject().getId());
        row.createCell(1).setCellValue(history.getId());

        // Status and Confidence
        row.createCell(2).setCellValue(history.getStatus());

        Cell confidenceCell = row.createCell(3);
        confidenceCell.setCellValue(history.getConfidence());
        if (history.getConfidence() < 0.5) {  // Low confidence threshold
          confidenceCell.setCellStyle(warningStyle);
        }

        // Level
        row.createCell(4).setCellValue(history.getLevel());

        // Timestamp
        Cell timestampCell = row.createCell(5);
        timestampCell.setCellValue(history.getDateCaptured());
        timestampCell.setCellStyle(dateStyle);

        // Image Availability
        row.createCell(6).setCellValue(history.getImage() != null ? "Yes" : "No");
      }
    }

    // Auto-size columns
    for (int i = 0; i < headers.length; i++) {
      sheet.autoSizeColumn(i);
    }
  }

  private void createEventAnalysisSheet(XSSFSheet sheet,
      XSSFCellStyle headerStyle,
      XSSFCellStyle dataStyle,
      XSSFCellStyle dateStyle,
      XSSFCellStyle eventStyle) {
    // Create headers
    String[] headers = {
        "Object ID", "Event ID", "Status", "Event Status", "Confidence",
        "Level", "Start Time", "End Time", "Duration (min)", "Image Available"
    };

    Row headerRow = sheet.createRow(0);
    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(headerStyle);
    }

    // Populate event details
    int rowNum = 1;
    for (ReportResponse.ObjectHistory objHistory : reportData.getObjects()) {
      for (Event event : objHistory.getEvents()) {
        Row row = sheet.createRow(rowNum++);

        // Object Identification
        row.createCell(0).setCellValue(objHistory.getInfraObject().getId());
        row.createCell(1).setCellValue(event.getId());

        // Status and Event Status
        row.createCell(2).setCellValue(event.getStatus());

        Cell eventStatusCell = row.createCell(3);
        eventStatusCell.setCellValue(event.getEventStatus());
        if ("SUCCESS".equals(event.getEventStatus())) {
          eventStatusCell.setCellStyle(eventStyle);
        }

        // Confidence
        Cell confidenceCell = row.createCell(4);
        confidenceCell.setCellValue(event.getConfidence());

        // Level
        row.createCell(5).setCellValue(event.getLevel());

        // Start Time
        Cell startTimeCell = row.createCell(6);
        startTimeCell.setCellValue(event.getDateCaptured());
        startTimeCell.setCellStyle(dateStyle);

        // End Time
        Cell endTimeCell = row.createCell(7);
        endTimeCell.setCellValue(event.getEndTime());
        endTimeCell.setCellStyle(dateStyle);

        // Duration in minutes (if both start and end times are available)
        if (event.getDateCaptured() != null && event.getEndTime() != null) {
          double durationMinutes = java.time.Duration.between(
              event.getDateCaptured(), event.getEndTime()).toMinutes();
          row.createCell(8).setCellValue(durationMinutes);
        } else {
          row.createCell(8).setCellValue("N/A");
        }

        // Image Availability
        row.createCell(9).setCellValue(event.getImage() != null ? "Yes" : "No");
      }
    }

    // Auto-size columns
    for (int i = 0; i < headers.length; i++) {
      sheet.autoSizeColumn(i);
    }
  }

  private void createDailyHistoryStatusSheet(XSSFWorkbook workbook,
      XSSFCellStyle headerStyle,
      XSSFCellStyle dataStyle) {
    XSSFSheet dailyStatusSheet = workbook.createSheet("Daily History Status");

    // Prepare headers
    String[] headers = {
        "Date",
        "Total Histories",
        "OK Count",
        "Not OK Count",
        "OK Percentage"
    };

    Row headerRow = dailyStatusSheet.createRow(0);
    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(headerStyle);
    }

    // Group histories by date
    Map<String, List<ReportResponse.History>> dailyHistories = reportData.getObjects().stream()
        .flatMap(objHistory -> objHistory.getHistories().stream())
        .collect(Collectors.groupingBy(
            history -> history.getDateCaptured().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
        ));

    // Populate daily status statistics
    int rowNum = 1;
    for (Map.Entry<String, List<ReportResponse.History>> entry : dailyHistories.entrySet()) {
      Row row = dailyStatusSheet.createRow(rowNum++);

      // Date
      row.createCell(0).setCellValue(entry.getKey());

      List<ReportResponse.History> dailyHistoryList = entry.getValue();

      // Total Histories
      row.createCell(1).setCellValue(dailyHistoryList.size());

      // Count OK and Not OK statuses
      long okCount = dailyHistoryList.stream()
          .filter(h -> "OK".equals(h.getStatus()))
          .count();
      long notOkCount = dailyHistoryList.size() - okCount;

      row.createCell(2).setCellValue(okCount);
      row.createCell(3).setCellValue(notOkCount);

      // OK Percentage
      double okPercentage = (okCount * 100.0) / dailyHistoryList.size();
      row.createCell(4).setCellValue(String.format("%.2f%%", okPercentage));
    }

    // Auto-size columns
    for (int i = 0; i < headers.length; i++) {
      dailyStatusSheet.autoSizeColumn(i);
    }
  }

  private void createEventStatusSheet(XSSFSheet sheet,
      XSSFCellStyle headerStyle,
      XSSFCellStyle dataStyle) {
    // Prepare headers
    String[] headers = {
        "Event Status",
        "Count",
        "Percentage",
        "Avg. Duration (min)",
        "Avg. Confidence"
    };

    Row headerRow = sheet.createRow(0);
    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(headerStyle);
    }

    // Group events by event status
    Map<String, List<Event>> eventsByStatus = reportData.getObjects().stream()
        .flatMap(objHistory -> objHistory.getEvents().stream())
        .collect(Collectors.groupingBy(Event::getEventStatus));

    // Calculate total events
    long totalEvents = eventsByStatus.values().stream()
        .mapToLong(List::size)
        .sum();

    // Populate event status statistics
    int rowNum = 1;
    for (Map.Entry<String, List<Event>> entry : eventsByStatus.entrySet()) {
      Row row = sheet.createRow(rowNum++);

      // Event Status
      row.createCell(0).setCellValue(entry.getKey());

      List<Event> eventList = entry.getValue();
      int eventCount = eventList.size();

      // Count
      row.createCell(1).setCellValue(eventCount);

      // Percentage
      double percentage = (eventCount * 100.0) / totalEvents;
      row.createCell(2).setCellValue(String.format("%.2f%%", percentage));

      // Average Duration
      double avgDuration = eventList.stream()
          .filter(e -> e.getDateCaptured() != null && e.getEndTime() != null)
          .mapToLong(e -> java.time.Duration.between(e.getDateCaptured(), e.getEndTime()).toMinutes())
          .average()
          .orElse(0);
      row.createCell(3).setCellValue(String.format("%.2f", avgDuration));

      // Average Confidence
      double avgConfidence = eventList.stream()
          .mapToDouble(Event::getConfidence)
          .average()
          .orElse(0);
      row.createCell(4).setCellValue(String.format("%.2f", avgConfidence));
    }

    // Auto-size columns
    for (int i = 0; i < headers.length; i++) {
      sheet.autoSizeColumn(i);
    }
  }

}