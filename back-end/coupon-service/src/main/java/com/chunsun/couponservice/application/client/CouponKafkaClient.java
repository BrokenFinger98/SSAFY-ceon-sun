package com.chunsun.couponservice.application.client;

import static com.chunsun.couponservice.application.dto.FeignDto.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "coupon-kafka-service")
public interface CouponKafkaClient {

	@PostMapping("/admin")
	CreateCouponFeignResponse createCoupon(@RequestBody final CreateCouponFeignRequest request);

	@DeleteMapping("/admin/{couponId}")
	void cancelCoupon(@PathVariable("couponId") final Long couponId);
}