package com.example.api_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {
    private final AuthenticateFilter authenticateFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_service", r -> r
                        .path("/api/auth/**")
                        .uri("lb://AUTH-SERVICE"))
                .route("user_service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.filter(authenticateFilter))
                        .uri("lb://USER-SERVICE"))
                .route("camera_service", r -> r
                        .path("/api/cameras/**")
                        .filters(f -> f.filter(authenticateFilter))
                        .uri("lb://CAMERA-SERVICE"))
                .route("infrastructure_service", r -> r
                        .path("/api/infrastructures/**")
                        .filters(f -> f.filter(authenticateFilter))
                        .uri("lb://INFRASTRUCTURE-SERVICE"))
                .route("report_service", r -> r
                        .path("/api/reports/**")
                        .filters(f -> f.filter(authenticateFilter))
                        .uri("lb://REPORT-SERVICE"))
                .route("notification_service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f.filter(authenticateFilter))
                        .uri("lb://NOTIFICATION-SERVICE"))
                .build();
    }
}
