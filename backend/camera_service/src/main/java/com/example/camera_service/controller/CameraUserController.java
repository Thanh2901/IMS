package com.example.camera_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.camera_service.dto.CameraUserDTO;
import com.example.camera_service.dto.response.ApiResponse;
import com.example.camera_service.dto.response.CameraResponse;
import com.example.camera_service.dto.response.CameraUserResponse;
import com.example.camera_service.service.impl.CameraUserServiceImpl;

@RestController
@RequestMapping("/api/cameras")
public class CameraUserController {
    private final CameraUserServiceImpl cameraUserService;

    @Autowired
    public CameraUserController(CameraUserServiceImpl cameraUserService) {
        this.cameraUserService = cameraUserService;
    }

    @GetMapping("/get_user/{camId}")
    public ResponseEntity<List<CameraUserResponse>> getUserByCamId(@PathVariable String camId) {
        return ResponseEntity.ok(cameraUserService.getUserByCamId(camId));
    }

    @GetMapping("/get_camera/{userId}")
    public ResponseEntity<List<CameraResponse>> getCameraByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(cameraUserService.getCameraByUserId(userId));
    }

    @GetMapping("/me")
    public ApiResponse<List<CameraResponse>> getCameraByMe(@RequestHeader("X-Credential-Id") String credentialId) {
        return ApiResponse.<List<CameraResponse>>builder()
                .data(cameraUserService.getCameraByUserId(credentialId))
                .build();
    }

    @PostMapping("/add_user_cam")
    public ResponseEntity<CameraUserResponse> addBothUserAndCam(@RequestBody CameraUserDTO cameraUserDTO) {
        return ResponseEntity.ok(cameraUserService.addUserCam(cameraUserDTO));
    }
}
