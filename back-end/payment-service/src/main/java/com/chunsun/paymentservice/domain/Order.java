package com.chunsun.paymentservice.domain;

import java.math.BigDecimal;

import com.chunsun.paymentservice.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "coupon_id")
	private Long couponId;

	@Column(name = "contracted_class_id", nullable = false)
	private Long contractedClassId;

	@Column(name = "count", nullable = false)
	private Integer count;

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(name = "merchant_uid", nullable = false, length = 50)
	private String merchantUid;

	@Column(name = "imp_uid", length = 50)
	private String impUid;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	public Order(Long memberId, Long couponId, Long contractedClassId, Integer count, BigDecimal amount,
		String merchantUid, String impUid, PaymentStatus status) {
		this.memberId = memberId;
		this.couponId = couponId;
		this.contractedClassId = contractedClassId;
		this.count = count;
		this.amount = amount;
		this.merchantUid = merchantUid;
		this.impUid = impUid;
		this.status = status;
	}
}
