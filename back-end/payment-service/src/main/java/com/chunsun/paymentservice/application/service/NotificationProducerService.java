package com.chunsun.paymentservice.application.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.chunsun.paymentservice.application.dto.NotificationDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducerService {
	private final KafkaTemplate<String, NotificationDto.RequestDto> kafkaTemplate;

	public Mono<String> sendNotification(NotificationDto.RequestDto requestDto) {
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