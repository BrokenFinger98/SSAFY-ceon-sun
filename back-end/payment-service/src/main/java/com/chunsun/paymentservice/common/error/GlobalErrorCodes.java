package com.chunsun.paymentservice.common.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorCodes implements ErrorCode {
    INTERNAL_SERVER_ERROR(BAD_REQUEST, "COMMON4001" , "내부 서버 오류"),

    NOT_FOUND_URL(NOT_FOUND, "COMMON4002", "존재하지 않는 url"),

    INVALID_JSON_DATA(BAD_REQUEST, "COMMON4003", "잘못된 형식의 JSON data"),

    INVALID_HEADER_DATA(BAD_REQUEST, "COMMON4004", "잘못된 형식의 Header data"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
