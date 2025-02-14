package com.chunsun.notificationservice.common.exception;

import com.chunsun.notificationservice.common.error.ErrorCode;

public class NotificationException extends BusinessException {
	public NotificationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
