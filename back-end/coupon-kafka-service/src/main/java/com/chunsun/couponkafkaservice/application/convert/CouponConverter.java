package com.chunsun.couponkafkaservice.application.convert;

import static com.chunsun.couponkafkaservice.application.dto.ServiceDto.*;
import static com.chunsun.couponkafkaservice.presentation.dto.ControllerDto.*;

import java.util.List;
import java.util.stream.Collectors;

import com.chunsun.couponkafkaservice.domain.Coupon;

public class CouponConverter {

	public static CreateCouponServiceRequest toCreateCouponServiceRequest(CreateCouponControllerRequest request) {
		return new CreateCouponServiceRequest(request.name(), request.discountRate(), request.validDays(),
			request.totalQuantity());
	}

	public static CreateCouponControllerResponse toCreateCouponControllerResponse(
		CreateCouponServiceResponse response) {
		return new CreateCouponControllerResponse(response.couponId(), response.name(), response.discountRate(),
			response.validDays(), response.totalQuantity(), response.remainingQuantity(), response.expiryDate());
	}

	public static Coupon toCoupon(CreateCouponServiceRequest request) {
		return new Coupon(request.name(), request.discountRate(), request.validDays(), request.totalQuantity());
	}

	public static CreateCouponServiceResponse toCreateCouponServiceResponse(Coupon coupon) {
		return new CreateCouponServiceResponse(coupon.getId(), coupon.getName(), coupon.getDiscountRate(),
			coupon.getValidDays(), coupon.getTotalQuantity(), coupon.getRemainingQuantity(), coupon.getExpiryDate());
	}

	public static SearchCouponsControllerResponse toSearchCouponsControllerResponse(
		SearchCouponsServiceResponse response) {

		List<SearchCouponControllerResponse> controllerCoupons = response.coupons().stream()
			.map((SearchCouponServiceResponse serviceCoupon) ->
				new SearchCouponControllerResponse(
					serviceCoupon.couponId(),
					serviceCoupon.name(),
					serviceCoupon.discountRate(),
					serviceCoupon.status(),
					serviceCoupon.expiryDate()
				))
			.collect(Collectors.toList());

		return new SearchCouponsControllerResponse(controllerCoupons);
	}

	public static UpdateCouponStatusServiceRequest toUpdateCouponStatusServiceRequest(
		UpdateCouponStatusControllerRequest request) {
		return new UpdateCouponStatusServiceRequest(request.memberId(), request.couponId());
	}
}
