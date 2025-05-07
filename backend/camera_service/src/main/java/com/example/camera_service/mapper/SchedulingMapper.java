package com.example.camera_service.mapper;

import org.springframework.stereotype.Component;

import com.example.camera_service.dto.SchedulingDTO;
import com.example.camera_service.entity.Camera;
import com.example.camera_service.entity.Scheduling;
import com.example.camera_service.repository.CameraRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SchedulingMapper {
    private final CameraRepository cameraRepository;

    public Scheduling toScheduling(SchedulingDTO schedulingDTO) {
        if (schedulingDTO == null) {
            return null;
        }
        Scheduling scheduling = new Scheduling();
        scheduling.setId(schedulingDTO.getId());
        if (schedulingDTO.getCameraId() != null && !schedulingDTO.getCameraId().isEmpty()) {
            Camera camera = cameraRepository
                    .findById(schedulingDTO.getCameraId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Camera not found with id: " + schedulingDTO.getCameraId()));
            scheduling.setCamera(camera);
        }
        scheduling.setStartTime(schedulingDTO.getStartTime());
        scheduling.setEndTime(schedulingDTO.getEndTime());
        scheduling.setVideoUrl(schedulingDTO.getVideoUrl());
        scheduling.setGpsLogsUrl(schedulingDTO.getGpsLogsUrl());
        return scheduling;
    }

    public SchedulingDTO schedulingDTO(Scheduling scheduling) {
        if (scheduling == null) {
            return null;
        }
        SchedulingDTO schedulingDTO = new SchedulingDTO();
        schedulingDTO.setId(scheduling.getId());
        schedulingDTO.setCameraId(
                scheduling.getCamera() != null
                        ? String.valueOf(scheduling.getCamera().getId())
                        : null);
        schedulingDTO.setStartTime(scheduling.getStartTime());
        schedulingDTO.setEndTime(scheduling.getEndTime());
        schedulingDTO.setVideoUrl(scheduling.getVideoUrl());
        schedulingDTO.setGpsLogsUrl(scheduling.getGpsLogsUrl());
        schedulingDTO.setSchedulingStatus(scheduling.getSchedulingStatus());
        return schedulingDTO;
    }
}
