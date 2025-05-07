package com.example.infrastructure_service.utils;

import ch.hsr.geohash.GeoHash;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class Utils {

  private static final double EARTH_RADIUS_METERS = 6371008.8;

  private static final String[] ALLOWED_IMAGE_TYPES = {
      "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
  };

  public static Double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = EARTH_RADIUS_METERS * c;
    // Làm tròn 4 chữ số thập phân
    return new BigDecimal(distance).setScale(4, RoundingMode.HALF_UP).doubleValue();
  }

  public static String generateGeoHash(double latitude, double longitude, int precision) {
    return GeoHash.withCharacterPrecision(latitude, longitude, precision).toBase32();
  }

  public static boolean isAllowedImageType(String contentType) {
    for (String type : ALLOWED_IMAGE_TYPES) {
      if (type.equals(contentType)) {
        return true;
      }
    }
    return false;
  }

}
