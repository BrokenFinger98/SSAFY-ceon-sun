package com.chunsun.notificationservice.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.notificationservice.application.dto.NotificationDto;
import com.chunsun.notificationservice.application.service.NotificationProducerService;
import com.chunsun.notificationservice.application.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationProducerService notificationProducerService;
	private final NotificationService notificationService;

	/**
	 * 테스트용으로 알림을 발송하는 메서드
	 * 요청 바디로 넘어온 {@link NotificationDto.RequestDto} 정보를 기반 Kafka Producer 알림을 비동기로 전송
	 *
	 * @Todo 이후 해당 메서드 및 NotificationProducerService 제거
	 * @param requestDto 알림 타입(type), 메시지, 대상 사용자 ID 등을 담는 DTO
	 * @return 전송 성공 시, "ok" 메시지를 포함한 Mono<ResponseEntity> 반환
	 */
	@PostMapping
	public Mono<ResponseEntity<String>> sendNotification(
		@RequestBody NotificationDto.RequestDto requestDto) {
		return notificationProducerService.sendNotification(requestDto)
			.map(ResponseEntity::ok);
	}

	/**
	 * 모든 사용자에게 알림(예: 쿠폰 관련)을 발송하는 예시 메서드
	 * 구현부는 실제 로직 없이 단순히 "SUCCESS" 응답을 반환
	 *
	 * @return "SUCCESS" 문자열을 포함한 200 OK 응답
	 */
	@PostMapping("/coupons/send/all")
	public ResponseEntity<String> sendCouponNotificationToAllUsers(@RequestBody NotificationDto.CouponRequestDto requestDto) {
		notificationService.sendCouponNotificationToAllUsers(requestDto);

		return ResponseEntity.ok("COUPON SEND ALL NOTIFICATIONS");
	}

	/**
	 * 특정 사용자(userId)의 모든 알림을 조회하여 반환
	 * 반환되는 알림은 생성 시간이 읽음 여부 및 최신 순에 맞춰 정렬됨
	 *
	 * @param userId 대상 사용자의 ID (문자열)
	 * @return 해당 사용자의 모든 알림 정보를 Flux 형태로 반환
	 */
	@GetMapping("/user/{userId}/all")
	public Flux<NotificationDto.ResponseDto> getAllNotifications(@PathVariable String userId) {
		return notificationService.getAllNotificationsOrdered(userId);
	}

	/**
	 * 특정 사용자(userId)가 읽지 않은 알림이 존재하는지 확인
	 *
	 * @param userId 대상 사용자의 ID
	 * @return 읽지 않은 알림이 하나라도 있으면 true, 없으면 false가 담긴 Mono<ResponseEntity> 반환
	 */
	@GetMapping("/user/{userId}/has-unread")
	public Mono<ResponseEntity<Boolean>> hasUnreadNotifications(@PathVariable String userId) {
		return notificationService.hasUnreadNotifications(userId)
			.map(ResponseEntity::ok);
	}

	/**
	 * 알림 상세 조회 메서드로, 조회 시 해당 알림의 isRead 값을 true로 업데이트
	 * notificationId에 해당하는 알림을 찾아 읽음 처리 후, 그 알림의 상세 정보를 반환
	 *
	 * @param notificationId 알림의 고유 식별자(ID)
	 * @return 알림 상세 정보(읽음 처리된 상태)가 담긴 Mono<ResponseEntity> 반환
	 */
	@GetMapping("/detail/{notificationId}")
	public Mono<ResponseEntity<NotificationDto.ResponseDto>> getNotificationDetail(
		@PathVariable String notificationId) {
		return notificationService.markNotificationAsRead(notificationId)
			.map(ResponseEntity::ok);
	}
}
