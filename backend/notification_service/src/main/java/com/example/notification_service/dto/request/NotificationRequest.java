package com.example.notification_service.dto.request;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class NotificationRequest<T> implements Serializable {

  private String title;
  private String body;
  private String image;
  private List<String> registrationTokens;
  private T data;
}
