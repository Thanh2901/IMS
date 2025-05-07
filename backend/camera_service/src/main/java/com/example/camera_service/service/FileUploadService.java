package com.example.camera_service.service;

import com.example.camera_service.dto.SchedulingDTO;
import com.example.camera_service.entity.Scheduling;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileUploadService {
  ResponseEntity<Map<String, String>> uploadVideoChunk(
      MultipartFile filePart, int chunkIndex, int totalChunks, String fileName, String scheduleId, String cameraId);

  ResponseEntity<Map<String, String>> uploadGpsFile(
      MultipartFile file, String fileName, String scheduleId, String cameraId, String startTime, String endTime);

  ResponseEntity<String> startProcessing(String scheduleId);

  ResponseEntity<Boolean> checkExistSchedule(String scheduleId);
}