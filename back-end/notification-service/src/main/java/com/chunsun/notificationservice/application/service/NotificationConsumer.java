package com.chunsun.notificationservice.application.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.notificationservice.application.convert.NotificationConverter;
import com.chunsun.notificationservice.application.dto.NotificationDto;
import com.chunsun.notificationservice.application.vo.NotificationType;
import com.chunsun.notificationservice.common.error.NotificationErrorCodes;
import com.chunsun.notificationservice.common.exception.NotificationException;
import com.chunsun.notificationservice.domain.entity.Notification;
import com.chunsun.notificationservice.domain.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {
	private final SseService sseService;
	private final NotificationRepository notificationRepository;

	@Transactional
	@KafkaListener(
		topics = "alarm",
		groupId = "notification-group",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void consumeAlarm(ConsumerRecord<String, NotificationDto.RequestDto> record, Acknowledgment ack) {
		NotificationDto.RequestDto event = record.value();
		log.info("알림 수신, type: {}, message: {}", event.getType(), event.getMessage());

		if (!NotificationType.isValidType(event.getType())) {
			throw new NotificationException(NotificationErrorCodes.INVALID_NOTIFICATION_TYPE);
		}

		Notification notification = NotificationConverter.toNotification(event);
		notificationRepository.save(notification);

		NotificationDto.ResponseDto responseDto = NotificationConverter.toResponseDto(notification);
		sseService.sendNotification(event.getTargetUserId(), responseDto.getId());

		ack.acknowledge();
		log.info("Kafka 오프셋 커밋 완료: partition={}, offset={}", record.partition(), record.offset());
	}
}
