package com.chunsun.couponkafkaservice.presentation;

import static com.chunsun.couponkafkaservice.application.convert.CouponConverter.toCreateCouponControllerResponse;
import static com.chunsun.couponkafkaservice.application.convert.CouponConverter.toCreateCouponServiceRequest;
import static com.chunsun.couponkafkaservice.application.dto.ServiceDto.*;
import static com.chunsun.couponkafkaservice.presentation.dto.ControllerDto.*;
import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.couponkafkaservice.application.service.CouponService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class CouponAdminController {

	private final CouponService couponService;

	@PostMapping
	public ResponseEntity<CreateCouponControllerResponse> createCoupon(
		@Valid @RequestBody final CreateCouponControllerRequest request) {
		CreateCouponServiceResponse response = couponService.createCoupon(toCreateCouponServiceRequest(request));
		return ResponseEntity.status(CREATED).body(toCreateCouponControllerResponse(response));
	}

	@DeleteMapping("/{couponId}")
	public ResponseEntity<Void> cancelCoupon(@PathVariable("couponId") final Long couponId) {
		couponService.deleteCoupon(couponId);
		return ResponseEntity.noContent().build();
	}
}
