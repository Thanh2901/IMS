package com.example.camera_service.controller;

import com.example.camera_service.dto.request.UpdateSchedulingRequest;
import com.example.camera_service.dto.request.UpdateVideoSchedule;
import com.example.camera_service.entity.Scheduling;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.camera_service.client.CronRecordClient;
import com.example.camera_service.dto.SchedulingDTO;
import com.example.camera_service.dto.request.CreateSchedulingRequest;
import com.example.camera_service.dto.response.ApiResponse;
import com.example.camera_service.service.CameraService;
import com.example.camera_service.service.SchedulingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cameras/scheduling")
@RequiredArgsConstructor
@Slf4j
public class SchedulingController {
    private final SchedulingService schedulingService;
    private final CronRecordClient cronRecordClient;
    private final CameraService cameraService;

    @PostMapping("")
    public ApiResponse<SchedulingDTO> createScheduling(@RequestBody CreateSchedulingRequest request) {

        SchedulingDTO schedulingDTO =
                new SchedulingDTO(request.getStartTime(), request.getEndTime(), request.getCameraId(), null, null);
        String rtspUrl = cameraService.getRtspUrlByCameraId(request.getCameraId());
        SchedulingDTO saveScheduling = schedulingService.createScheduling(schedulingDTO);
        cronRecordClient.updateRecordingTimeWithParams(
                request.getStartTime(), request.getEndTime(), rtspUrl, saveScheduling.getId());
        return ApiResponse.<SchedulingDTO>builder()
                .message("Scheduling created scuccessfully")
                .data(saveScheduling)
                .build();
    }

    @GetMapping("/update-cron")
    public ResponseEntity<SchedulingDTO> updateScheduling(
            @RequestParam(value = "schedulingId", required = true) String schedulingId,
            @RequestParam(value = "videoUrl", required = false) String videoUrl,
            @RequestParam(value = "gpsUrl", required = false) String gpsUrl) {
        log.info(
                "Received update request - schedulingId: {}, videoUrl: {}, gpsUrl: {}", schedulingId, videoUrl, gpsUrl);
        SchedulingDTO updatedScheduling = schedulingService.updateScheduling(schedulingId, videoUrl, gpsUrl);
        return new ResponseEntity<>(updatedScheduling, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SchedulingDTO>> getAllSchedulings() {
        return new ResponseEntity<>(schedulingService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchedulingDTO> getSchedulingById(@PathVariable String id) {
        return new ResponseEntity<>(schedulingService.getScheduling(id), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSchedulingById(@PathVariable String id) {
        schedulingService.deleteScheduling(id);
        return new ResponseEntity<>("Delete successfully", HttpStatus.OK);
    }

    // variable: st, et ->
    @GetMapping("/filter/time")
    public ApiResponse<List<SchedulingDTO>> filterTime(
            @RequestParam String startTime, @RequestParam String endTime, @RequestParam String cameraId) {
        return ApiResponse.<List<SchedulingDTO>>builder()
                .message("Filtering scheduling by timer and cameraId successfully")
                .data(schedulingService.getScheduleByTimeFilter(startTime, endTime, cameraId))
                .build();
    }

    @GetMapping("/camera/{cameraId}")
    public ApiResponse<List<SchedulingDTO>> getSchedulingByCameraId(@PathVariable String cameraId) {
        return ApiResponse.<List<SchedulingDTO>>builder()
                .message("Get scheduling by camera id successfully")
                .data(schedulingService.getSchedulingByCameraId(cameraId))
                .build();
    }

    @PostMapping("/public/update")
    public ApiResponse<SchedulingDTO> updateScheduling(@RequestBody UpdateVideoSchedule request) {
        System.out.println(request);
        return ApiResponse.<SchedulingDTO>builder()
                .message("Update scheduling successfully")
                .data(schedulingService.updateVideoSchedule(request, true))
                .build();
    }

}
