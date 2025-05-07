package com.example.infrastructure_service.exception;

import com.example.infrastructure_service.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(value = RuntimeException.class)
  public ResponseEntity<String> handleRunTimeException(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(value = CustomRuntimeException.class)
  public ResponseEntity<ApiResponse<String>> handleCustomRuntimeException(CustomRuntimeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<String>builder()
        .code(ex.getCode())
        .message("Have a nice day")
        .data(ex.getMessage())
        .build());
  }
}
