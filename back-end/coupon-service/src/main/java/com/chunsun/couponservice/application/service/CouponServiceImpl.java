package com.chunsun.couponservice.application.service;

import static com.chunsun.couponservice.application.dto.ServiceDto.*;
import static com.chunsun.couponservice.application.convert.DtoConverter.*;
import static com.chunsun.couponservice.application.dto.FeignDto.*;

import org.springframework.stereotype.Service;

import com.chunsun.couponservice.application.client.CouponKafkaClient;
import com.chunsun.couponservice.application.client.NotificationClient;
import com.chunsun.couponservice.common.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {

	private final CouponKafkaClient couponKafkaClient;
	// private final NotificationClient notificationClient;
	private final RedisService redisService;
	private final KafkaProducerService kafkaProducerService;


	@Override
	public CreateCouponServiceResponse createCoupon(final CreateCouponServiceRequest request) {
		final CreateCouponFeignResponse feignResponse = couponKafkaClient.createCoupon(
			toCreateCouponFeignRequest(request));
		final CreateCouponServiceResponse response = toCreateCouponServiceResponse(feignResponse);
		try {
			redisService.saveCouponInfo(toCouponCreateRedis(response));
		} catch (BusinessException e) {
			couponKafkaClient.cancelCoupon(response.couponId());
			throw e;
		}
		// TODO: 알림 전송 개발 해야함
		// notificationClient.sendNotification(new NotificationRequestDto());
		return response;
	}

	@Override
	public void issueCoupon(final IssueCouponServiceRequest request) {
		redisService.issueCoupon(toCouponIssueRedis(request));
		kafkaProducerService.sendCouponIssuedEvent(toIssueCouponRecord(request));
	}
}
