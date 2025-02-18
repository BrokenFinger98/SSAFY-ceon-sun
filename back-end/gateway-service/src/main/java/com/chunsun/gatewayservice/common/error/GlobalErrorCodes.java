package com.chunsun.gatewayservice.common.error;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorCodes implements ErrorCode {
	INTERNAL_SERVER_ERROR(HttpStatus.BAD_REQUEST, "COMMON4001", "내부 서버 오류"),

	NOT_FOUND_URL(HttpStatus.NOT_FOUND, "COMMON4002", "존재하지 않는 URL"),

	INVALID_JSON_DATA(HttpStatus.BAD_REQUEST, "COMMON4003", "잘못된 형식의 JSON DATA"),

	INVALID_HEADER_DATA(HttpStatus.BAD_REQUEST, "COMMON4004", "잘못된 형식의 Header DATA"),

	INVALID_LOGIN_TOKEN(HttpStatus.BAD_REQUEST, "COMMON4005", "토큰 정보가 유효하지 않습니다"),

	ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "COMMON4006", "잘못된 권한 요청입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
