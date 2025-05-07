package com.example.camera_service.dto.request;

import java.util.List;

import com.example.camera_service.utils.CameraStatus;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CameraUpdateRequest {
    private String name;
    private String ipAddress;
    private String port;
    private String username;
    private String password;
    private CameraStatus cameraStatus;
    private List<CameraUserRequest> cameraUserList;
}
