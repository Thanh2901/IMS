package com.example.infrastructure_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

//model is used for parse json form kafka to object java
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonObject {

  @JsonProperty("info")
  private Info info;

  @JsonProperty("images")
  private List<Image> images;

  @JsonProperty("annotations")
  private List<Annotation> annotations;

  @JsonProperty("categories")
  private List<Category> categories;

  // Getters and setters
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Info {

    @JsonProperty("cameraId")
    private String cameraId;

    @JsonProperty("scheduleId")
    private String scheduleId;

    // Getters and setters
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Image {

    @JsonProperty("date_captured")
    private String dateCaptured;
    @JsonProperty("path_url")
    private String pathUrl;
    private int id;
    private Integer frame;

    // Getters and setters
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Annotation {
    @JsonProperty("image_id")
    private int imageId;
    @JsonProperty("category_id")
    private int categoryId;
    private String status;
    private int id;
    private double conf;
    private Location location;
    private float[] bbox;
    @JsonProperty("real_width")
    private Double realWidth;
    @JsonProperty("real_height")
    private Double realHeight;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Location {

    private double latitude;
    private double longitude;

    // Getters and setters
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Category {

    private String supercategory;
    private int id;
    private String name;

    // Getters and setters
  }
}
