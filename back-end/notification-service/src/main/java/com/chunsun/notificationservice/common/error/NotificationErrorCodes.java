package com.chunsun.notificationservice.common.error;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationErrorCodes implements ErrorCode {
	INVALID_NOTIFICATION_TYPE(HttpStatus.BAD_REQUEST, "NOTIFICATION4001", "잘못된 알림 타입입니다."),
	NOT_FOUND_NOTIFICATION(HttpStatus.NOT_FOUND, "NOTIFICATION4002", "해당 알람이 존재하지 않습니다."),
	NOT_FOUND_NOTIFICATION_USER(HttpStatus.NOT_FOUND, "NOTIFICATION4003", "전송할 유저가 존재하지 않습니다."),
	;
	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
