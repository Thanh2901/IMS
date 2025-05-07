package com.example.camera_service.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.camera_service.dto.CameraUserDTO;
import com.example.camera_service.dto.response.CameraResponse;
import com.example.camera_service.dto.response.CameraUserResponse;
import com.example.camera_service.entity.Camera;
import com.example.camera_service.entity.CameraUser;
import com.example.camera_service.exception.AppException;
import com.example.camera_service.exception.ErrorCode;
import com.example.camera_service.mapper.CameraMapper;
import com.example.camera_service.mapper.CameraUserMapper;
import com.example.camera_service.repository.CameraRepository;
import com.example.camera_service.repository.CameraUserRepository;
import com.example.camera_service.service.CameraUserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CameraUserServiceImpl implements CameraUserService {
    private final CameraUserRepository cameraUserRepository;
    private final CameraUserMapper cameraUserMapper;
    private final CameraMapper cameraMapper;
    private final CameraRepository cameraRepository;

    @Autowired
    public CameraUserServiceImpl(
            CameraUserRepository cameraUserRepository,
            CameraUserMapper cameraUserMapper,
            CameraMapper cameraMapper,
            CameraRepository cameraRepository) {
        this.cameraUserRepository = cameraUserRepository;
        this.cameraUserMapper = cameraUserMapper;
        this.cameraMapper = cameraMapper;
        this.cameraRepository = cameraRepository;
    }

    public List<CameraUserResponse> getUserByCamId(String camId) {
        List<CameraUser> cameraUsers = cameraUserRepository.getUserByCamId(camId);
        return cameraUsers.stream().map(cameraUserMapper::toCameraUserResponse).collect(Collectors.toList());
    }

    public List<CameraResponse> getCameraByUserId(String userId) {
        // Fetch camera IDs for the given user
        List<String> cameraIdList = cameraUserRepository.getCameraByUserId(userId);
        if (cameraIdList.isEmpty()) {
            // Return empty list or throw an exception if needed
            return new ArrayList<>();
        }
        // Fetch Camera entities using the camera IDs
        List<Camera> cameras = new ArrayList<>();
        for (String cameraId : cameraIdList) {
            Camera camera = cameraRepository
                    .findById(cameraId)
                    .orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_EXISTED)); // Handle camera not found
            cameras.add(camera);
        }
        // Map Camera entities to CameraResponse DTOs
        return cameras.stream().map(cameraMapper::toCameraResponse).collect(Collectors.toList());
    }

    public CameraUserResponse addUserCam(CameraUserDTO cameraUserDTO) {
        CameraUser cameraUser = new CameraUser();
        Camera camera = cameraRepository
                .findById(cameraUserDTO.getCameraId())
                .orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_EXISTED));
        cameraUser.setCamera(camera);
        cameraUser.setUserId(cameraUserDTO.getUserId());
        return cameraUserMapper.toCameraUserResponse(cameraUserRepository.save(cameraUser));
    }
}
