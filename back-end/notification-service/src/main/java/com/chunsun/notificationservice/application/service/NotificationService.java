package com.chunsun.notificationservice.application.service;

import com.chunsun.notificationservice.application.dto.NotificationDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationService {
	void sendCouponNotificationToAllUsers(NotificationDto.CouponRequestDto requestDto);

	Flux<NotificationDto.ResponseDto> getAllNotificationsOrdered(String content);

	Mono<Boolean> hasUnreadNotifications(String userId);

	Mono<NotificationDto.ResponseDto> markNotificationAsRead(String notificationId);
}
