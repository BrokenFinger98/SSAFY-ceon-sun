package com.chunsun.notificationservice.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.notificationservice.application.convert.NotificationConverter;
import com.chunsun.notificationservice.application.dto.NotificationDto;
import com.chunsun.notificationservice.application.vo.NotificationType;
import com.chunsun.notificationservice.common.error.NotificationErrorCodes;
import com.chunsun.notificationservice.common.exception.NotificationException;
import com.chunsun.notificationservice.config.feign.MemberFeignClient;
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
	private final MemberFeignClient memberFeignClient;
	private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
	private final SseService sseService;

	/**
	 * 1) 모든 STUDENT 유저 조회
	 * 2) 해당 유저들에 대해 알람 추가
	 */
	public void sendCouponNotificationToAllUsers(NotificationDto.CouponRequestDto requestDto) {
		// 모든 유저 조회 - 멤버 서버에서 모든 유저 id List 받아오기
		var memberIdList = memberFeignClient.getStudentIds().getStudentsId();
		System.out.println(memberIdList);

		if (memberIdList.isEmpty()) {
			throw new NotificationException(NotificationErrorCodes.NOT_FOUND_NOTIFICATION_USER);
		}

		log.info("총 {}명의 유저에게 알림 저장 시도", memberIdList.size());

		List<Notification> notifications = memberIdList.stream()
			.map(id -> new Notification().builder()
					.targetUserId(String.valueOf(id))
					.type(NotificationType.COUPON.name())
					.message(requestDto.getMessage())
					.isRead(false)
					.build()
			)
			.collect(Collectors.toList());

		notificationRepository.insert(notifications);
		log.info("유저 알림 저장 성공");

		sendNotifications(notifications);
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

	private void sendNotifications(List<Notification> notificationList) {
		log.info("모든 유저 쿠폰 알림 발송 시작");

		List<CompletableFuture<Void>> futures = notificationList.stream()
			.map(notification -> CompletableFuture.runAsync(() -> {
				try {
					sseService.sendNotification(notification.getTargetUserId(), notification.getId());
				} catch (Exception e) {
					log.error("전송 실패 userId={}, error={}", notification, e.getMessage());
				}
			}, executorService))
			.collect(Collectors.toList());

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
			.thenRun(() -> log.info("모든 유저에게 알림 전송 완료"));
	}
}
