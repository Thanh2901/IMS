package com.example.infrastructure_service.service;

import com.example.infrastructure_service.client.CameraClient;
import com.example.infrastructure_service.dto.response.CameraResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CameraUserService {
  private final CameraClient cameraClient;

  public List<String> getCameraData(String userId){
    ResponseEntity<List<CameraResponse>> responses = cameraClient.getCameraByUser(userId);

    return responses.getBody().stream()
        .map(CameraResponse::getId)
        .collect(Collectors.toList());
  }

  public void checkCameraUser(String cameraId, String userId){

    List<String> cameraIds = getCameraData(userId);

    if(!cameraIds.contains(cameraId) && cameraId!=null && !cameraId.isEmpty()) throw new RuntimeException("You dont have access to this camera");
  }

}
