package com.chunsun.couponservice.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.chunsun.couponservice.application.dto.NotificationRequestDto;

@FeignClient(name = "notification-service")
public interface NotificationClient {

	@PostMapping("/coupons/send/all")
	String sendNotification(@RequestBody final NotificationRequestDto notificationRequestDto);
}
