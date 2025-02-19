package com.chunsun.paymentservice.application.client;

import static com.chunsun.paymentservice.application.dto.FeignDto.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "coupon-kafka-service")
public interface CouponKafkaClient {

	@PutMapping("/coupons")
	Void updateCouponStatus(@RequestBody final UpdateCouponStatusRequest request);
}
