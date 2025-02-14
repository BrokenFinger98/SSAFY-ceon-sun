package com.chunsun.couponservice.presentation;

import static com.chunsun.couponservice.application.convert.DtoConverter.toCreateCouponControllerResponse;
import static com.chunsun.couponservice.application.convert.DtoConverter.toCreateCouponServiceRequest;
import static com.chunsun.couponservice.application.convert.DtoConverter.toIssueCouponServiceRequest;
import static com.chunsun.couponservice.application.dto.ServiceDto.*;
import static com.chunsun.couponservice.presentation.dto.ControllerDto.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.couponservice.application.service.CouponService;
import com.chunsun.couponservice.application.service.RedisService;
import com.chunsun.couponservice.common.resolver.UserId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/coupons")
@RestController
public class CouponController {

	private final CouponService couponService;
	private final RedisService redisService;

	@PostMapping("/admin")
	public ResponseEntity<CreateCouponControllerResponse> createCoupon(
		@Valid @RequestBody final CreateCouponControllerRequest request) {
		CreateCouponServiceResponse response = couponService.createCoupon(toCreateCouponServiceRequest(request));
		return ResponseEntity.status(CREATED).body(toCreateCouponControllerResponse(response));
	}

	@UserId
	@PostMapping
	public ResponseEntity<Void> issueCoupon(@UserId final Long memberId,
		@Valid @RequestBody final issueCouponControllerRequest request) {
		couponService.issueCoupon(toIssueCouponServiceRequest(memberId, request));
		return ResponseEntity.status(CREATED).build();
	}

	@GetMapping
	public ResponseEntity<SearchCouponsControllerResponse> searchCoupon(){
		SearchCouponsControllerResponse response = redisService.searchCoupons();
		return ResponseEntity.status(OK).body(response);
	}
}