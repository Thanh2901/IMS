package com.example.user_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String credentialId;

    @Column(nullable = false)
    private String fullName;

    private String phone;
    private String address;

    @OneToOne(cascade = CascadeType.ALL)
    private UserSetting userSetting;
}
