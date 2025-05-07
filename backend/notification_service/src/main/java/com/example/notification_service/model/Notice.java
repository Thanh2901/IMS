package com.example.notification_service.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notice implements Serializable {

  private String subject;

  private String content;

  private Map<String, String> data;

  private List<String> registrationTokens;
}
