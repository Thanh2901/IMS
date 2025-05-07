package com.example.camera_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CronUpdateRequest {
    private String startTimeInput;
    private String endTimeInput;
}
