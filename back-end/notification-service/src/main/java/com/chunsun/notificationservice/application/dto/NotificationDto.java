package com.chunsun.notificationservice.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NotificationDto {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CouponRequestDto {
		private String message;
	}

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

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResponseDto {
		private String id;
		private String sendUserId;
		private String targetUserId;
		private String type;
		private String message;
		private boolean isRead;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class EventDto {
		private String id;
		private String sendUserId;
		private String targetUserId;
		private String type;
		private String message;
		private boolean isRead;
	}
}
