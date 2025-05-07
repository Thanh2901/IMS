package com.example.infrastructure_service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.runtime.ObjectMethods;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioService {

  private final MinioClient minioClient;
  @Value("${minio.url}")
  private String minioUrl;
  private final ObjectMapper objectMapper;

  public String uploadFile(MultipartFile file, String bucketName, String fileName) {

    try {
      try(InputStream inputStream = file.getInputStream()){
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(inputStream, file.getSize(), -1)
                .contentType(file.getContentType())
                .build()
        );
      }

      return minioUrl + "/" + bucketName + "/" + fileName;

    }catch (Exception e) {
      throw new RuntimeException("Error uploading file to MinIO", e);
    }
  }

  public String uploadObject(Object object, String bucketName, String fileName) {

    try {
      String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
      byte[] jsonBytes = jsonContent.getBytes(StandardCharsets.UTF_8);
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucketName)
              .object(fileName)
              .stream(new ByteArrayInputStream(jsonBytes), jsonBytes.length, -1)
              .contentType("application/json")
              .build()
      );

      return minioUrl + "/" + bucketName + "/" + fileName;

    }catch (Exception e) {
      throw new RuntimeException("Error uploading file to MinIO", e);
    }
  }
}
