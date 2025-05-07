package com.example.auth_service.dto.response;

public enum JwtErrorCode {
  VALID_TOKEN("JWT_00", "Valid Token"),
  EXPIRED_TOKEN("JWT_001", "Token has expired"),
  MALFORMED_TOKEN("JWT_002", "Token is malformed"),
  INVALID_SIGNATURE("JWT_003", "Token signature is invalid"),
  UNSUPPORTED_TOKEN("JWT_004", "Token is unsupported"),
  CLAIM_ERROR("JWT_005", "Invalid or missing claims");

  private final String code;
  private final String message;

  JwtErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}

