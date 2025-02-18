package com.chunsun.couponkafkaservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import com.chunsun.couponkafkaservice.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "coupons") // DB 테이블명과 동일
public class Coupon extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "discount_rate", nullable = false)
	private Integer discountRate;

	@Column(name = "valid_days", nullable = false)
	private Integer validDays;

	@Column(name = "total_quantity", nullable = false)
	private Integer totalQuantity;

	@Column(name = "remaining_quantity", nullable = false)
	private Integer remainingQuantity;

	@Column(name = "expiry_date", nullable = false)
	private LocalDateTime expiryDate;

	public Coupon(final String name, final Integer discountRate, final Integer validDays, final Integer totalQuantity) {
		this.name = name;
		this.discountRate = discountRate;
		this.validDays = validDays;
		this.totalQuantity = totalQuantity;
		this.remainingQuantity = totalQuantity;
		this.expiryDate = LocalDateTime.now().plusDays(validDays);
	}
}