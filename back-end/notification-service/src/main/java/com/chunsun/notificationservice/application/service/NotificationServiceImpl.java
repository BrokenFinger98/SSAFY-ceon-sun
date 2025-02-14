package com.chunsun.notificationservice.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.notificationservice.application.convert.NotificationConverter;
import com.chunsun.notificationservice.application.dto.NotificationDto;
import com.chunsun.notificationservice.common.error.NotificationErrorCodes;
import com.chunsun.notificationservice.common.exception.NotificationException;
import com.chunsun.notificationservice.domain.entity.Notification;
import com.chunsun.notificationservice.domain.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
	private final NotificationRepository notificationRepository;

	/**
	 * 1) 모든 STUDENT 유저 조회
	 * 2) 해당 유저들에 대해 알람 추가
	 */
	public void sendCouponNotificationToAllUsers(String messageContent) {
		// 모든 유저 조회 - 멤버 서버에서 모든 유저 id List 받아오기

		// 각 유저에 대한 알림 메시지 생성
		List<Notification> notifications = new ArrayList<>();
		// for (User user : allUsers) {
		// 	Notification notification = new Notification(user.getId(), messageContent, LocalDateTime.now());
		// 	notifications.add(notification);
		//
		// 	// SSE를 통해 실시간 알림 전송
		// 	sseEmitterService.sendNotification(user.getId(), "쿠폰이 발급되었습니다!");
		// }
		//
		// // 한 번에 NoSQL에 저장
		// notificationRepository.insertAll(notifications);
	}

	/**
	 * 유저ID로 모든 알림 목록 조회 → ResponseDto 목록
	 */
	@Override
	public Flux<NotificationDto.ResponseDto> getAllNotificationsOrdered(String userId) {
		List<NotificationDto.ResponseDto> unread = notificationRepository.findByTargetUserIdAndIsReadFalse(userId)
			.stream()
			.map(NotificationConverter::toResponseDto)
			.toList();
		List<NotificationDto.ResponseDto> read = notificationRepository.findByTargetUserIdAndIsReadTrue(userId)
			.stream()
			.map(NotificationConverter::toResponseDto)
			.toList();

		List<NotificationDto.ResponseDto> combined = new ArrayList<>();
		combined.addAll(unread);
		combined.addAll(read);
		
		return Flux.fromIterable(combined);
	}

	@Override
	public Mono<Boolean> hasUnreadNotifications(String userId) {
		return Mono.fromCallable(() ->
			notificationRepository.existsByTargetUserIdAndIsReadFalse(userId));
	}

	@Transactional
	public Mono<NotificationDto.ResponseDto> markNotificationAsRead(String notificationId) {
		return Mono.fromCallable(() -> {
			Optional<Notification> optional = notificationRepository.findById(notificationId);
			if (!optional.isPresent()) {
				throw new NotificationException(NotificationErrorCodes.NOT_FOUND_NOTIFICATION);
			}

			Notification notification = optional.get();
			if (!notification.isRead()) {
				notification.setRead(true);
				notificationRepository.save(notification);
			}

			return NotificationConverter.toResponseDto(notification);
		});
	}
}
