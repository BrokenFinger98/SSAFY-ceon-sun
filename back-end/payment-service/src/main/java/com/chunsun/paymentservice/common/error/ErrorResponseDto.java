package com.chunsun.paymentservice.common.error;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

public record ErrorResponseDto (
    String code,
    String message,
    LocalDateTime serverDateTime) {

    public ErrorResponseDto(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage(), LocalDateTime.now());
    }

    public static ResponseEntity<ErrorResponseDto> of(ErrorCode errorCode) {

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(new ErrorResponseDto(errorCode));
    }
}
