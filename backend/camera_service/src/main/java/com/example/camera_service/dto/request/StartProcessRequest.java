package com.example.camera_service.dto.request;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StartProcessRequest {
  private String camera_id;
  private String schedule_id;
  private String video_path;
  private String gps_path;
  private List<String> model_types;
  private List<String> camera_types;
}
