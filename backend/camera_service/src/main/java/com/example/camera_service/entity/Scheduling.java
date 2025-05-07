package com.example.camera_service.entity;

import com.example.camera_service.utils.SchedulingStatus;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scheduling")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Scheduling extends AbstractEntity {
    @Id
    private String id;

    private String startTime;
    private String endTime;

    @ManyToOne
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @Enumerated(EnumType.STRING)
    private SchedulingStatus schedulingStatus;
    private String videoUrl;
    private String gpsLogsUrl;


}
