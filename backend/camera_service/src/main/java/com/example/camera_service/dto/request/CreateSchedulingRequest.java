package com.example.camera_service.dto.request;

import lombok.Data;

@Data
public class CreateSchedulingRequest {
    private String startTime;
    private String endTime;
    private String cameraId;
}
