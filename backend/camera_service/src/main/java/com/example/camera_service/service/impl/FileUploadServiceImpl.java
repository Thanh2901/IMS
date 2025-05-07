package com.example.camera_service.service.impl;

import com.example.camera_service.client.UploadClient;
import com.example.camera_service.dto.SchedulingDTO;
import com.example.camera_service.dto.request.UpdateSchedulingRequest;
import com.example.camera_service.dto.request.UpdateVideoSchedule;
import com.example.camera_service.entity.Scheduling;
import com.example.camera_service.repository.SchedulingRepository;
import com.example.camera_service.service.FileUploadService;
import com.example.camera_service.service.SchedulingService;
import com.example.camera_service.utils.SchedulingStatus;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

  @Value("${my-ip}")
  private String myIp;

  private final MinioClient minioClient;
  private final SchedulingService schedulingService;
  private final UploadClient uploadClient;
  private final SchedulingRepository schedulingRepository;
  private static final String UPLOAD_DIR = "uploads/";
  private static final String BUCKET_NAME = "monitor-test";

  @Override
  public ResponseEntity<Map<String, String>> uploadVideoChunk(
      MultipartFile filePart, int chunkIndex, int totalChunks, String fileName, String scheduleId, String cameraId) {
    Map<String, String> response = new HashMap<>();

    try {
      if (chunkIndex < 0 || totalChunks <= 0 || fileName.isEmpty()) {
        response.put("error", "Invalid input data");
        return ResponseEntity.badRequest().body(response);
      }

      File uploadDir = new File(UPLOAD_DIR);
      if (!uploadDir.exists()) {
        uploadDir.mkdirs();
      }

      Path tempFilePath = Path.of(UPLOAD_DIR, fileName + ".part" + chunkIndex);
      try (InputStream inputStream = filePart.getInputStream()) {
        Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
      }

      log.info("Video chunk {} uploaded successfully for file {}", chunkIndex, fileName);

      if (chunkIndex == totalChunks - 1) {
        log.info("Final video chunk received, merging file: {}", fileName);
        File finalFile = mergeChunks(fileName, totalChunks);
        log.info("Video file {} merged successfully, uploading to MinIO...", fileName);

        uploadToMinio(finalFile, BUCKET_NAME, fileName, "video/mp4");

        String pathVideo = myIp + "/monitor-test/" + fileName;

        SchedulingDTO schedulingDTO = schedulingService.updateVideoSchedule(new UpdateVideoSchedule(scheduleId, pathVideo), false);
        String responseUpload  = uploadClient.startProcessing(schedulingDTO.getCameraId(), schedulingDTO.getId(), schedulingDTO.getVideoUrl(), schedulingDTO.getGpsLogsUrl());

        log.info("Response from upload client: {}", responseUpload);

        Files.deleteIfExists(finalFile.toPath());
        response.put("message", "Video file uploaded successfully");
        return ResponseEntity.ok(response);
      }

      response.put("message", "Video chunk " + chunkIndex + " uploaded successfully");
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Video upload failed: {}", e.getMessage(), e);
      response.put("error", "Video upload failed: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @Override
  public ResponseEntity<Map<String, String>> uploadGpsFile(
      MultipartFile file, String fileName, String scheduleId, String cameraId, String startTime, String endTime) {
    Map<String, String> response = new HashMap<>();
    try {
      log.info("cameraId: {}", cameraId);
      if (fileName.isEmpty() || file.isEmpty()) {
        response.put("error", "Invalid input data");
        return ResponseEntity.badRequest().body(response);
      }

      log.info("Uploading GPS file {} to MinIO...", fileName);
      try (InputStream inputStream = file.getInputStream()) {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(fileName)
                .stream(inputStream, file.getSize(), -1)
                .contentType("application/json")
                .build()
        );
      }

      String pathGps = myIp + "/monitor-test/" + fileName;

      schedulingService.uploadSchedule(new UpdateSchedulingRequest(scheduleId, pathGps, null, cameraId, startTime, endTime));
      log.info("GPS file {} uploaded to MinIO successfully", fileName);
      response.put("message", "GPS file uploaded successfully");
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("GPS upload failed: {}", e.getMessage(), e);
      response.put("error", "GPS upload failed: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @Override
  public ResponseEntity<String> startProcessing(String scheduleId) {
    Scheduling scheduling = schedulingRepository.findById(scheduleId)
        .orElseThrow(() -> new RuntimeException("Scheduling not found with id: " + scheduleId));
    String responseUpload  = uploadClient.startProcessing(scheduling.getCamera().getId(), scheduling.getId(), scheduling.getVideoUrl(), scheduling.getGpsLogsUrl());

    return ResponseEntity.ok(responseUpload);
  }

  @Override
  public ResponseEntity<Boolean> checkExistSchedule(String scheduleId) {
    Scheduling scheduling = schedulingRepository.findById(scheduleId).orElse(null);
    if(scheduling!= null && scheduling.getVideoUrl() != null && scheduling.getGpsLogsUrl() != null && scheduling.getSchedulingStatus() == SchedulingStatus.PENDING) {
      return ResponseEntity.ok(true);
    }
    return ResponseEntity.badRequest().body(false);
  }

  private File mergeChunks(String fileName, int totalChunks) throws IOException {
    File mergedFile = new File(UPLOAD_DIR + fileName);
    try (FileOutputStream fos = new FileOutputStream(mergedFile)) {
      for (int i = 0; i < totalChunks; i++) {
        Path chunkPath = Path.of(UPLOAD_DIR, fileName + ".part" + i);
        if (!Files.exists(chunkPath)) {
          throw new FileNotFoundException("Missing chunk: " + i);
        }
        Files.copy(chunkPath, fos);
        Files.delete(chunkPath);
      }
    }
    return mergedFile;
  }

  private void uploadToMinio(File file, String bucketName, String objectName, String contentType) throws Exception {
    try (InputStream is = Files.newInputStream(file.toPath())) {
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucketName)
              .object(objectName)
              .stream(is, file.length(), -1)
              .contentType(contentType)
              .build()
      );
      log.info("File {} uploaded to MinIO bucket {} successfully", objectName, bucketName);
    }
  }
}