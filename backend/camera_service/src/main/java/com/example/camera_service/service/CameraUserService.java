package com.example.camera_service.service;

import java.util.List;

import com.example.camera_service.dto.CameraUserDTO;
import com.example.camera_service.dto.response.CameraResponse;
import com.example.camera_service.dto.response.CameraUserResponse;

public interface CameraUserService {
    List<CameraUserResponse> getUserByCamId(String camId);

    List<CameraResponse> getCameraByUserId(String userId);

    CameraUserResponse addUserCam(CameraUserDTO cameraUserDTO);
}
