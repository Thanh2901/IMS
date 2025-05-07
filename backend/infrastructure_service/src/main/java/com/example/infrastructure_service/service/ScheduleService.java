package com.example.infrastructure_service.service;

import com.example.infrastructure_service.client.CameraClient;
import com.example.infrastructure_service.client.MinioService;
import com.example.infrastructure_service.dto.InfraLogRecord;
import com.example.infrastructure_service.dto.SchedulingDTO;
import com.example.infrastructure_service.dto.request.EndProcessRequest;
import com.example.infrastructure_service.model.InfraObject;
import com.example.infrastructure_service.repository.InfraObjectRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

  private final InfraObjectRepository infraObjectRepository;
  private final CameraClient cameraClient;
  private final NotificationService notificationService;
  private final MinioService minioService;

  public void endProcessSchedule(EndProcessRequest request) {

    System.out.println(request);
    //update scheduling with url gps, video and update time
    SchedulingDTO schedulingDTO =  cameraClient.updateScheduling(request).getData();

    // get infra objects that have not been updated
//    SchedulingDTO schedulingDTO = cameraClient.getSchedulingById(request.getScheduleId()).getBody();
    LocalDateTime startTime = LocalDateTime.parse(schedulingDTO.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    List<InfraObject> infraObjects = infraObjectRepository.getNotUpdateInfraObjects(schedulingDTO.getCameraId(), startTime, LocalDateTime.now());

    infraObjects.forEach(obj -> {
      obj.setIsUpdated(false);
    });

    infraObjectRepository.saveAll(infraObjects);

    String responseUpload = minioService.uploadObject(
        infraObjects, "infra-logs", request.getScheduleId() + ".json");
    log.info("responseUpload: {}", responseUpload);
    notificationService.createScheduleNotification(schedulingDTO);
  }
}
