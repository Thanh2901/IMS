package com.example.infrastructure_service.exception;

public class CustomRuntimeException extends RuntimeException {
  private final int code;

  public CustomRuntimeException(int code, String message) {
    super(message);
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
