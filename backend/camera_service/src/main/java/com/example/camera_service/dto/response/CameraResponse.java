package com.example.camera_service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.camera_service.utils.CameraStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CameraResponse {
    private String id;
    private String name;
    private String rtsp;
    private String ipAddress;
    private String port;
    private String userName;
    private CameraStatus cameraStatus;
    private List<CameraUserResponse> cameraUserList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
