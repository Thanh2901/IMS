package com.example.notification_service.exception;

import com.example.notification_service.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(value = RuntimeException.class)
  public ApiResponse<String> handleUserNotFoundException(RuntimeException ex) {
    return ApiResponse.<String>builder()
        .code(HttpStatus.BAD_REQUEST.value())
        .message(ex.getMessage())
        .build();
  }
}
