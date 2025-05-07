package com.example.auth_service.dto.response;


import com.example.auth_service.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    String userId;
    String fullName;
    String phone;
    String address;
    String email;
}
