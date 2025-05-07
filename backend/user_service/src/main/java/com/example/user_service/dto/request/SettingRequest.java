package com.example.user_service.dto.request;

import lombok.Data;

@Data
public class SettingRequest {
  private Boolean settingAddInfrastructure;
  private Boolean settingNotification;
}
