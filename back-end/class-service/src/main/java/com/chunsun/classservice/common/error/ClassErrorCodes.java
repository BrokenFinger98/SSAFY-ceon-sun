package com.chunsun.classservice.common.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClassErrorCodes implements ErrorCode {

    INVALID_REQUEST(BAD_REQUEST, "CLASS4001", "올바르지 않은 요청입니다."),
    INVALID_AUTHORITY(FORBIDDEN, "CLASS4002", "해당 리소스에 접근할 권한이 없습니다."),
    DUPLICATE_CLASS_REQUEST(BAD_REQUEST, "CLASS4003", "이미 존재하는 요청입니다."),
    CONTRACTED_CLASS_NOT_FOUND(BAD_REQUEST, "CLASS4004", "존재하지 않는 contractedClassId 입니다."),
    SOURCE_CODE_NOT_FOUND(BAD_REQUEST, "CLASS4005", "존재하지 않는 sourceCodeId 입니다."),
    OPENVIDU_SESSION_ERROR(INTERNAL_SERVER_ERROR, "CLASS4006", "OpenVidu Session을 가져오지 못했습니다."),
    OPENVIDU_SESSION_CLOSE_ERROR(INTERNAL_SERVER_ERROR, "CLASS4007", "OpenVidu Session을 종료하는데 실패했습니다."),
    NOT_ENOUGH_REMAINCLASS(BAD_REQUEST, "CLASS4008", "수업 가능 횟수가 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
