package com.example.camera_service.service.impl;

import com.example.camera_service.dto.request.UpdateSchedulingRequest;
import com.example.camera_service.dto.request.UpdateVideoSchedule;
import java.util.List;

import java.util.Optional;
import java.util.UUID;

import com.example.camera_service.utils.SchedulingStatus;
import org.springframework.stereotype.Service;

import com.example.camera_service.dto.SchedulingDTO;
import com.example.camera_service.entity.Camera;
import com.example.camera_service.entity.Scheduling;
import com.example.camera_service.mapper.SchedulingMapper;
import com.example.camera_service.repository.CameraRepository;
import com.example.camera_service.repository.SchedulingRepository;
import com.example.camera_service.service.SchedulingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulingServiceImpl implements SchedulingService {

    private final SchedulingMapper schedulingMapper;
    private final SchedulingRepository schedulingRepository;
    private final CameraRepository cameraRepository;

    @Override
    public SchedulingDTO createScheduling(SchedulingDTO schedulingDTO) {
        Scheduling scheduling = schedulingMapper.toScheduling(schedulingDTO);
        Camera camera = cameraRepository
                .findById(schedulingDTO.getCameraId())
                .orElseThrow(() -> new RuntimeException("Camera Not Found"));
        scheduling.setCamera(camera);
        scheduling.setId(UUID.randomUUID().toString());
        scheduling.setSchedulingStatus(SchedulingStatus.RUNNING);
        Scheduling savedScheduling = schedulingRepository.save(scheduling);
        return schedulingMapper.schedulingDTO(savedScheduling);
    }

    @Override
    public SchedulingDTO updateScheduling(String schedulingId, String videoUrl, String gpsUrl) {
        if (schedulingId == null) {
            throw new IllegalArgumentException("Scheduling ID cannot be null");
        }

        Scheduling scheduling = schedulingRepository
                .findById(schedulingId)
                .orElseThrow(() -> new IllegalArgumentException("Scheduling not found with id: " + schedulingId));

        boolean isUpdated = false;
        if (videoUrl != null && !videoUrl.equals(scheduling.getVideoUrl())) {
            scheduling.setVideoUrl(videoUrl);
            isUpdated = true;
        }
        if (gpsUrl != null && !gpsUrl.equals(scheduling.getGpsLogsUrl())) {
            scheduling.setGpsLogsUrl(gpsUrl);
            isUpdated = true;
        }

        if (isUpdated) {
            Scheduling updatedScheduling = schedulingRepository.save(scheduling);

            if (updatedScheduling.getVideoUrl() != null && updatedScheduling.getGpsLogsUrl() != null) {
                updatedScheduling.setSchedulingStatus(SchedulingStatus.DONE);
                updatedScheduling = schedulingRepository.save(updatedScheduling);
            }
            return schedulingMapper.schedulingDTO(updatedScheduling);
        }

        return schedulingMapper.schedulingDTO(scheduling);
    }
    @Override
    public void deleteScheduling(String schedulingId) {
        schedulingRepository.deleteById(schedulingId);
    }

    @Override
    public SchedulingDTO getScheduling(String schedulingId) {
        return schedulingMapper.schedulingDTO(schedulingRepository
                .findById(schedulingId)
                .orElseThrow(() -> new RuntimeException("Scheduling not found with id: " + schedulingId)));
    }

    @Override
    public List<SchedulingDTO> getAll() {
        return schedulingRepository.findAll().stream()
                .map(schedulingMapper::schedulingDTO)
                .toList();
    }

    @Override
    public List<SchedulingDTO> getScheduleByTimeFilter(String startTime, String endTime, String cameraId) {
        return schedulingRepository.getScheduleByTimeFilter(startTime, endTime, cameraId).stream()
                .map(schedulingMapper::schedulingDTO)
                .toList();
    }

    @Override
    public List<SchedulingDTO> getSchedulingByCameraId(String cameraId) {
        return schedulingRepository.getSchedulingByCamera(cameraId).stream()
                .map(schedulingMapper::schedulingDTO)
                .toList();
    }

    @Override
    public void uploadSchedule(UpdateSchedulingRequest request) {

        Camera camera = cameraRepository.findById(request.getCameraId())
            .orElseThrow(() -> new RuntimeException("Camera Not Found"));

            Scheduling newScheduling = new Scheduling();
            newScheduling.setId(request.getScheduleId());
            newScheduling.setCamera(camera);
            newScheduling.setVideoUrl(request.getVideoUrl());
            newScheduling.setGpsLogsUrl(request.getGpsUrl());
            newScheduling.setStartTime(request.getStartTime());
            newScheduling.setEndTime(request.getEndTime());
            newScheduling.setSchedulingStatus(SchedulingStatus.RUNNING);
            schedulingRepository.save(newScheduling);
    }

    @Override
    public SchedulingDTO updateVideoSchedule(UpdateVideoSchedule request, Boolean done) {

        Scheduling scheduling = schedulingRepository.findById(request.getScheduleId())
            .orElseThrow(() -> new RuntimeException("Scheduling Not Found"));

        scheduling.setVideoUrl(request.getVideoUrl());
        if(done) scheduling.setSchedulingStatus(SchedulingStatus.DONE);
        Scheduling updatedScheduling = schedulingRepository.save(scheduling);
        return schedulingMapper.schedulingDTO(updatedScheduling);

    }
}
