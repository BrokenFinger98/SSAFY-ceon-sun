package com.chunsun.couponkafkaservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import com.chunsun.couponkafkaservice.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_coupon")
public class MemberCoupon extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "coupon_id",
		nullable = false,
		foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
	)
	private Coupon coupon;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private CouponStatus status;

	@Column(name = "expiry_date", nullable = false)
	private LocalDateTime expiryDate;

	public void changeStatus(CouponStatus status) {
		this.status = status;
	}
}
