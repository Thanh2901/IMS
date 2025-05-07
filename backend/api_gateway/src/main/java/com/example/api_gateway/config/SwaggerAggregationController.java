package com.example.api_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api-docs")
@RequiredArgsConstructor
public class SwaggerAggregationController {

  private final WebClient.Builder webClientBuilder;

  @GetMapping("/{serviceName}")
  public Mono<String> getServiceDocs(@PathVariable String serviceName) {
    String serviceUrl = switch (serviceName) {
      case "auth" -> "http://AUTH-SERVICE/v3/api-docs";
      case "user" -> "http://USER-SERVICE/v3/api-docs";
      case "camera" -> "http://CAMERA-SERVICE/v3/api-docs";
      case "infrastructure" -> "http://INFRASTRUCTURE-SERVICE/v3/api-docs";
      case "report" -> "http://REPORT-SERVICE/v3/api-docs";
      case "notification" -> "http://NOTIFICATION-SERVICE/v3/api-docs";
      default -> throw new IllegalArgumentException("Unknown service: " + serviceName);
    };

    return webClientBuilder.build()
        .get()
        .uri(serviceUrl)
        .retrieve()
        .bodyToMono(String.class);
  }
}