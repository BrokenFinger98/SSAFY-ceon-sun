package com.chunsun.couponkafkaservice.application.service;

import static com.chunsun.couponkafkaservice.application.convert.CouponConverter.toCoupon;
import static com.chunsun.couponkafkaservice.application.convert.CouponConverter.toCreateCouponServiceResponse;
import static com.chunsun.couponkafkaservice.application.dto.ServiceDto.*;
import static com.chunsun.couponkafkaservice.common.error.CouponKafkaErrorCodes.INVALID_COUPON_DELETE_REQUEST;
import static com.chunsun.couponkafkaservice.common.error.CouponKafkaErrorCodes.INVALID_COUPON_STATUS_UPDATE_REQUEST;
import static com.chunsun.couponkafkaservice.domain.CouponStatus.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.couponkafkaservice.common.exception.BusinessException;
import com.chunsun.couponkafkaservice.domain.Coupon;
import com.chunsun.couponkafkaservice.domain.CouponRepository;
import com.chunsun.couponkafkaservice.domain.CouponStatus;
import com.chunsun.couponkafkaservice.domain.MemberCoupon;
import com.chunsun.couponkafkaservice.domain.MemberCouponRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {

	private final CouponRepository couponRepository;
	private final MemberCouponRepository memberCouponRepository;

	@Transactional
	@Override
	public CreateCouponServiceResponse createCoupon(final CreateCouponServiceRequest request) {
		final Coupon coupon = toCoupon(request);
		couponRepository.save(coupon);
		return toCreateCouponServiceResponse(coupon);
	}

	@Transactional
	@Override
	public void deleteCoupon(final Long couponId) {
		final Coupon coupon = couponRepository.findById(couponId)
			.orElseThrow(() -> new BusinessException(INVALID_COUPON_DELETE_REQUEST));
		couponRepository.delete(coupon);
	}

	@Transactional
	@Override
	public SearchCouponsServiceResponse searchCoupons(final Long memberId) {
		final List<MemberCoupon> memberCoupons = memberCouponRepository
			.findByMemberIdAndStatus(memberId, UNUSED);
		final List<SearchCouponServiceResponse> couponResponses = new ArrayList<>();

		for (final MemberCoupon memberCoupon : memberCoupons) {
			if (memberCoupon.getExpiryDate().isBefore(LocalDateTime.now())) {
				memberCoupon.changeStatus(EXPIRED);
			} else {
				couponResponses.add(new SearchCouponServiceResponse(
					memberCoupon.getCoupon().getId(),
					memberCoupon.getCoupon().getName(),
					memberCoupon.getCoupon().getDiscountRate(),
					memberCoupon.getStatus().toString(),
					memberCoupon.getExpiryDate()
				));
			}
		}

		return new SearchCouponsServiceResponse(couponResponses);
	}

	@Transactional
	@Override
	public void updateCouponStatus(final UpdateCouponStatusServiceRequest request) {
		final MemberCoupon memberCoupon = memberCouponRepository.findByMemberIdAndCoupon_Id(request.memberId(),
			request.couponId()).orElseThrow(() -> new BusinessException(INVALID_COUPON_STATUS_UPDATE_REQUEST));
		memberCoupon.changeStatus(USED);
	}
}
