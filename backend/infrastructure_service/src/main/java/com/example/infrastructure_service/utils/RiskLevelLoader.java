package com.example.infrastructure_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;

public class RiskLevelLoader {
  private static final Map<String, Integer> riskLevels;

  static {
    riskLevels = loadRiskLevels();
  }

  private static Map<String, Integer> loadRiskLevels() {
    ObjectMapper mapper = new ObjectMapper();
    try {

      InputStream is = new ClassPathResource("risk_levels.json").getInputStream();
      if (is == null) { throw new RuntimeException("Unable to load risk levels.json");}

      return mapper.readValue(is, Map.class);

    }catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static int getRiskLevels(String name) {
    return riskLevels.getOrDefault(name, 0);
  }
}
