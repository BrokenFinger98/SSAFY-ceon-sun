package com.chunsun.couponkafkaservice.application.service;

import static com.chunsun.couponkafkaservice.application.dto.ServiceDto.*;

public interface CouponService {

	CreateCouponServiceResponse createCoupon(final CreateCouponServiceRequest request);

	void deleteCoupon(final Long couponId);

	SearchCouponsServiceResponse searchCoupons(final Long memberId);

	void updateCouponStatus(final UpdateCouponStatusServiceRequest request);
}
