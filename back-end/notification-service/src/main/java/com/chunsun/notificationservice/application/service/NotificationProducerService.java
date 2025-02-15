package com.chunsun.notificationservice.application.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.chunsun.notificationservice.application.dto.NotificationDto;
import com.chunsun.notificationservice.application.vo.NotificationType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducerService {
	private final KafkaTemplate<String, NotificationDto.RequestDto> kafkaTemplate;

	// 해당 부분은 이후 삭제 예정

	/**
	 * 1) RequestDto → Notification 저장
	 * 2) Notification → EventDto 변환 후 Kafka 전송
	 * 3) Notification → ResponseDto 변환하여 반환
	 */
	public Mono<String> sendNotification(NotificationDto.RequestDto requestDto) {
		NotificationType.isValidType(requestDto.getType());

		return Mono.create(sink -> {
			kafkaTemplate.send("alarm", requestDto)
				.whenComplete((result, ex) -> {
					if (ex == null) {
						log.info("Kafka 메시지 전송 성공: {}", result.getProducerRecord().value());
						sink.success("SUCCESS");
					} else {
						log.error("Kafka 메시지 전송 실패: {}", ex.getMessage());
						sink.success("FAILURE");
					}
				});
		});
	}
}
