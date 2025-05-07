package com.example.infrastructure_service.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class MapClient {
  private final RestClient restClient;
  private final ObjectMapper objectMapper;

  @Value("${mapbox.access.token}")
  private String key;

  public String getAddress(double latitude, double longitude) {
    String mapUrl = "https://mapapis.openmap.vn/v1/geocode/reverse?format=google&latlng={lat},{long}&apikey=" + key;
    try {
      String response = restClient.get()
          .uri(mapUrl, Map.of("lat", latitude, "long", longitude))
          .retrieve()
          .body(String.class);

      JsonNode root = objectMapper.readTree(response);
      JsonNode results = root.path("results");
      if (results.isArray() && results.size() > 0) {
        return results.get(0).path("formatted_address").asText(null); // nếu không có thì sẽ trả về null
      } else {
        return null;
      }
    } catch (Exception e) {
      return null;
    }
  }

}
