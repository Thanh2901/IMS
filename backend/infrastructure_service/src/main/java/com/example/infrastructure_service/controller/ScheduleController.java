package com.example.infrastructure_service.controller;

import com.example.infrastructure_service.dto.ApiResponse;
import com.example.infrastructure_service.dto.request.EndProcessRequest;
import com.example.infrastructure_service.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/infrastructures/public/schedule")
public class ScheduleController {

  private final ScheduleService scheduleService;

  @PostMapping("/end")
  public ApiResponse<String> processEnd(@RequestBody EndProcessRequest request) {

    scheduleService.endProcessSchedule(request);

    return ApiResponse.<String>builder()
        .message("Process end successfully, create log and save to minio")
        .data("Process successfully")
        .build();
  }
}
