package com.example.user_service.config;

import com.example.user_service.dto.response.UserResponse;
import com.example.user_service.model.Users;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public static UserResponse map(Users user) {
        return UserResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .build();
    }
}
