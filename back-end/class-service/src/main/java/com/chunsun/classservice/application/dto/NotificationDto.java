package com.chunsun.classservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NotificationDto {
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RequestDto {
		private String sendUserId;
		private String targetUserId;
		private String type;
		private String message;
	}
}

