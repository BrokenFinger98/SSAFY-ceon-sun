package com.chunsun.notificationservice.common.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	HttpStatus getHttpStatus();

	String getCode();

	String getMessage();
}