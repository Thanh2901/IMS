package com.example.camera_service.dto.addOn;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class Location {
    double latitude;
    double longitude;

    @Override
    public String toString() {
        return String.format("Latitude: %.6f, Longitude: %.6f", latitude, longitude);
    }
}
