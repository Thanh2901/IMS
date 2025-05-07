package com.example.infrastructure_service.controller;

import com.example.infrastructure_service.dto.ApiResponse;
import com.example.infrastructure_service.model.InfraObjectProcess;
import com.example.infrastructure_service.service.InfraProcessingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/infrastructures/process")
@RequiredArgsConstructor
public class ProcessController {

  private final InfraProcessingService infraProcessingService;

  @PostMapping("/{processId}")
  public ApiResponse<InfraObjectProcess> processInfraObjectProcess(@PathVariable String processId) {
    return ApiResponse.<InfraObjectProcess>builder()
        .message("Process infra object process successfully with id: " + processId)
        .data(infraProcessingService.processOneInfraObjectProcess(processId))
        .build();
  }

  @GetMapping({"/schedule"})
  public ApiResponse<List<InfraObjectProcess>> getAllProcessBySchedule(@RequestParam String scheduleId, @RequestParam(required = false) String status, @RequestParam(required = false) String processStatus, @RequestParam(required = false) String eventStatus) {
    return ApiResponse.<List<InfraObjectProcess>>builder()
        .message("Get all process by schedule successfully")
        .data(infraProcessingService.getInfraObjectProcessBySchedule(scheduleId, status, processStatus, eventStatus))
        .build();
  }

  @GetMapping({"/schedule/{scheduleId}"})
  public ApiResponse<List<InfraObjectProcess>> getAllProcessBySchedule(@PathVariable String scheduleId) {
    return ApiResponse.<List<InfraObjectProcess>>builder()
        .message("Get all process by schedule successfully")
        .data(infraProcessingService.getAllProcessBySchedule(scheduleId))
        .build();
  }

  @PatchMapping({"/reject/{processId}"})
  public ApiResponse<InfraObjectProcess> rejectProcess(@PathVariable String processId) {
    return ApiResponse.<InfraObjectProcess>builder()
        .message("Reject process successfully")
        .data(infraProcessingService.rejectProcess(processId))
        .build();
  }

  @PostMapping("/schedule/{scheduleId}")
  public ApiResponse<List<InfraObjectProcess>> processAllInfraBySchedule(@PathVariable String scheduleId) {
    return ApiResponse.<List<InfraObjectProcess>>builder()
        .message("Process infra object process successfully with id: " + scheduleId)
        .data(infraProcessingService.processAllInfraBySchedule(scheduleId))
        .build();
  }

}
