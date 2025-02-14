package com.chunsun.notificationservice.application.vo;

import java.util.Arrays;

public enum NotificationType {
	COUPON,
	CLASS,
	CHAT,
	PAYMENT;

	public static boolean isValidType(String typeName) {
		return Arrays.stream(NotificationType.values())
			.anyMatch(type -> type.name().equalsIgnoreCase(typeName));
	}
}
