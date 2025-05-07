package com.example.camera_service.controller;

import com.example.camera_service.dto.SchedulingDTO;
import com.example.camera_service.entity.Scheduling;
import com.example.camera_service.service.FileUploadService;
import com.example.camera_service.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cameras/public")
@RequiredArgsConstructor
public class FileController {

  private final FileUploadService fileUploadService;

  @PostMapping("/upload/video/chunk")
  public ResponseEntity<Map<String, String>> uploadVideoChunk(
      @RequestParam("filePart") MultipartFile filePart,
      @RequestParam("chunkIndex") int chunkIndex,
      @RequestParam("totalChunks") int totalChunks,
      @RequestParam("fileName") String fileName,
      @RequestParam("scheduleId") String scheduleId,
      @RequestParam("cameraId") String cameraId) {
    return fileUploadService.uploadVideoChunk(filePart, chunkIndex, totalChunks, fileName, scheduleId, cameraId);
  }

  @PostMapping("/upload/gps")
  public ResponseEntity<Map<String, String>> uploadGpsFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("fileName") String fileName,
      @RequestParam("scheduleId") String scheduleId,
      @RequestParam("cameraId") String cameraId,
      @RequestParam("startTime") String startTime,
      @RequestParam("endTime") String endTime
  ) {
    return fileUploadService.uploadGpsFile(file, fileName, scheduleId, cameraId, startTime, endTime);
  }

  @PostMapping("/process/{scheduleId}")
  public ResponseEntity<String> startProcessing(
      @PathVariable String scheduleId){
    return fileUploadService.startProcessing(scheduleId);
  }

  @GetMapping("/schedule/{scheduleId}")
  public ResponseEntity<Boolean> checkExistSchedule(
      @PathVariable String scheduleId){
    return fileUploadService.checkExistSchedule(scheduleId);
  }
}