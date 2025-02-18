package com.chunsun.couponkafkaservice.application.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.couponkafkaservice.application.dto.BulkInsertCouponDto;
import com.chunsun.couponkafkaservice.application.dto.IssueCouponRecord;
import com.chunsun.couponkafkaservice.infrastructure.JdbcRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaEventListener {

	private final JdbcRepository jdbcRepository;
	private final ObjectMapper objectMapper;

	@Transactional
	@KafkaListener(
		groupId = "coupon-issue",
		topics = "coupon",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void consume(final List<String> messages, final Acknowledgment acknowledgment) {
		final List<IssueCouponRecord> issueCouponRecords = getIssueCouponRecords(messages);
		final List<BulkInsertCouponDto> bulkCoupons = getBulkInsertCouponDtos(issueCouponRecords);
		try {
			jdbcRepository.bulkInsert(bulkCoupons);
			jdbcRepository.updateRemainingQuantities(bulkCoupons);
			acknowledgment.acknowledge();
		} catch (final Exception e) {
			log.error("대량 쿠폰 삽입 처리 중 오류 발생: {}", e.getMessage(), e);
		}
	}

	private static List<BulkInsertCouponDto> getBulkInsertCouponDtos(final List<IssueCouponRecord> issueCouponRecords) {
		return issueCouponRecords.stream()
			.map(record -> new BulkInsertCouponDto(
				record.couponId(),
				record.memberId(),
				record.validDays()
			))
			.collect(Collectors.toList());
	}

	private List<IssueCouponRecord> getIssueCouponRecords(final List<String> messages) {
		return messages.stream().map(message -> {
			try {
				return objectMapper.readValue(message, IssueCouponRecord.class);
			} catch (JsonProcessingException e) {
				log.error("메시지를 IssueCouponRecord로 변환 실패: {}", message, e);
				return null;
			}
		}).filter(record -> record != null).collect(Collectors.toList());
	}
}
