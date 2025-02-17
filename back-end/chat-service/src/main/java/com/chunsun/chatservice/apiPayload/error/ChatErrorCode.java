package com.chunsun.chatservice.apiPayload.error;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements ErrorCode{
	INVALID_TOKEN(HttpStatus.BAD_REQUEST, "CHAT4001", "유효하지 않은 토큰 정보입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
