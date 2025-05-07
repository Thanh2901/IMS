package com.example.camera_service.service;

import com.example.camera_service.dto.request.UpdateSchedulingRequest;
import com.example.camera_service.dto.request.UpdateVideoSchedule;
import com.example.camera_service.entity.Scheduling;
import java.util.List;

import com.example.camera_service.dto.SchedulingDTO;
import org.hibernate.sql.Update;

public interface SchedulingService {
    SchedulingDTO createScheduling(SchedulingDTO schedulingDTO);

    SchedulingDTO updateScheduling(String schedulingId, String videoUrl, String gpsUrl);

    void deleteScheduling(String schedulingId);

    SchedulingDTO getScheduling(String schedulingId);

    List<SchedulingDTO> getAll();

    List<SchedulingDTO> getScheduleByTimeFilter(String startTime, String endTime, String cameraId);

    List<SchedulingDTO> getSchedulingByCameraId(String cameraId);

    void uploadSchedule(UpdateSchedulingRequest request);

    SchedulingDTO updateVideoSchedule(UpdateVideoSchedule request, Boolean done);
}
