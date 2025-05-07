package com.example.camera_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    CAMERA_NOT_EXISTED(1005, "Camera not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    CAMERA_EXISTED(1006, "Camera already exists", HttpStatus.BAD_REQUEST),
    NAME_CAMERA_EXISTED(1008, "Name already exists", HttpStatus.CONFLICT),
    IP_CAMERA_EXISTED(1009, "IP already exists", HttpStatus.CONFLICT),
    RTSP_NOT_FOUND(1009, "server can't find path", HttpStatus.BAD_REQUEST),
    PATH_ALREADY_EXISTED(1010, "path already exists", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
