package com.chunsun.couponservice.application.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.chunsun.couponservice.application.dto.IssueCouponRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {


	private static final String TOPIC = "coupon";
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public void sendCouponIssuedEvent(final IssueCouponRecord issueCouponRecord) {
		try {
			final String jsonValue = objectMapper.writeValueAsString(issueCouponRecord);
			kafkaTemplate.executeInTransaction(operations ->
				operations.send(TOPIC, issueCouponRecord.couponId().toString(), jsonValue)
			);
		} catch (JsonProcessingException e) {
			log.error("IssueCouponRecord를 JSON 문자열로 변환하는데 실패하였습니다.", e);
			throw new RuntimeException(e);
		}
	}
}
