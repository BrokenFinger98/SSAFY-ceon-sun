package com.chunsun.couponkafkaservice.presentation;

import static com.chunsun.couponkafkaservice.application.convert.CouponConverter.toSearchCouponsControllerResponse;
import static com.chunsun.couponkafkaservice.presentation.dto.ControllerDto.*;
import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.couponkafkaservice.application.service.CouponService;
import com.chunsun.couponkafkaservice.common.resolver.UserId;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/coupons")
@RestController
public class CouponController {

	private final CouponService couponService;

	@UserId
	@GetMapping("/members")
	public ResponseEntity<SearchCouponsControllerResponse> searchCouponsByMember(@UserId final Long memberId) {
		SearchCouponsControllerResponse response =
			toSearchCouponsControllerResponse(couponService.searchCoupons(memberId));
		return ResponseEntity.status(OK).body(response);
	}
}
