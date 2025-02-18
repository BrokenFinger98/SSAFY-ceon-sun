package com.chunsun.couponkafkaservice.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
	List<MemberCoupon> findByMemberIdAndStatus(Long memberId, CouponStatus status);
	Optional<MemberCoupon> findByMemberIdAndCoupon_Id(Long memberId, Long couponId);
}
