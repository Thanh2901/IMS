package com.example.camera_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSchedulingRequest {
  private String scheduleId;
  private String gpsUrl;
  private String videoUrl;
  private String cameraId;
  private String startTime;
  private String endTime;
}
