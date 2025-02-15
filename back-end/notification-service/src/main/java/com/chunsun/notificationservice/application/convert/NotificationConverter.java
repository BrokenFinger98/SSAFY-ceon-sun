package com.chunsun.notificationservice.application.convert;

import com.chunsun.notificationservice.application.dto.NotificationDto;
import com.chunsun.notificationservice.domain.entity.Notification;

public class NotificationConverter {

	public static Notification toNotification(NotificationDto.RequestDto requestDto) {
		return Notification.builder()
			.sendUserId(requestDto.getSendUserId())
			.targetUserId(requestDto.getTargetUserId())
			.type(requestDto.getType())
			.message(requestDto.getMessage())
			.isRead(false)
			.build();
	}

	public static NotificationDto.ResponseDto toResponseDto(Notification notification) {
		return NotificationDto.ResponseDto.builder()
			.id(notification.getId())
			.sendUserId(notification.getSendUserId())
			.targetUserId(notification.getTargetUserId())
			.type(notification.getType())
			.message(notification.getMessage())
			.isRead(notification.isRead())
			.build();
	}
}
