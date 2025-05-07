package com.example.infrastructure_service.client;

import com.example.infrastructure_service.dto.ApiResponse;
import com.example.infrastructure_service.dto.SchedulingDTO;
import com.example.infrastructure_service.dto.request.EndProcessRequest;
import com.example.infrastructure_service.dto.response.CameraResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "camera-service")
public interface CameraClient {
  @GetMapping("/api/cameras/get_camera/{userId}")
    ResponseEntity<List<CameraResponse>> getCameraByUser(@PathVariable String userId);
  @GetMapping("/api/cameras/scheduling/{id}")
  ResponseEntity<SchedulingDTO> getSchedulingById(@PathVariable String id);
  @PostMapping("/api/cameras/scheduling/public/update")
  ApiResponse<SchedulingDTO> updateScheduling(@RequestBody EndProcessRequest request);
}

