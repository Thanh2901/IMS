package com.example.auth_service.dto.response;

import io.jsonwebtoken.Claims;

public class JwtValidationResult {
  private final boolean valid;
  private final Claims claims;
  private final JwtErrorCode errorCode;

  private JwtValidationResult(boolean valid, Claims claims, JwtErrorCode errorCode) {
    this.valid = valid;
    this.claims = claims;
    this.errorCode = errorCode;
  }

  public static JwtValidationResult success(Claims claims,JwtErrorCode errorCode ) {
    return new JwtValidationResult(true, claims, errorCode);
  }

  public static JwtValidationResult failure(JwtErrorCode errorCode) {
    return new JwtValidationResult(false, null, errorCode);
  }

  public boolean isValid() {
    return valid;
  }

  public Claims getClaims() {
    return claims;
  }

  public JwtErrorCode getErrorCode() {
    return errorCode;
  }
}
