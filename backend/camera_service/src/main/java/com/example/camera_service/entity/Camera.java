package com.example.camera_service.entity;

import java.util.List;

import jakarta.persistence.*;

import com.example.camera_service.utils.CameraStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "camera")
public class Camera extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String rtsp;
    private String ipAddress;
    private String port;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private CameraStatus cameraStatus;

    @OneToMany(mappedBy = "camera")
    private List<CameraUser> cameraUserList;
}
