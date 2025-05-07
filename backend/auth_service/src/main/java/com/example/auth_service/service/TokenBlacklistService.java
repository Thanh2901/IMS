package com.example.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {
    private final RedisTemplate<Object, Object> redisTemplate;

    public void blacklistToken(String token, long expiryTimeInSeconds) {
        redisTemplate.opsForValue().set(token, "blacklisted", Duration.ofMillis(expiryTimeInSeconds));
    }

    public boolean isTokenBlackListed(String token) {
        var blacklistedToken = redisTemplate.opsForValue().get(token);
        log.info("Fetching token from redis: {}", blacklistedToken);
        return blacklistedToken != null;
    }
}
