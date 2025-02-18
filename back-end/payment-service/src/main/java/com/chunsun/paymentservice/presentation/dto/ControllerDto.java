package com.chunsun.paymentservice.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ControllerDto() {

	public record ValidPaymentControllerRequest(
		@NotNull(message = "impUid 값이 없습니다.")
		String impUid,

		@NotNull(message = "merchantUid 값이 없습니다.")
		String merchantUid,

		@NotNull(message = "amount 값이 없습니다.")
		@Min(10000)
		BigDecimal amount,

		Long couponId,

		@NotNull(message = "count 값이 없습니다.")
		@Min(1)
		Integer count,

		@NotNull(message = "contractedClassId 값이 없습니다.")
		Long contractedClassId
	){
	}

	public record ValidPaymentControllerResponse(
		String impUid,
		String merchantUid,
		BigDecimal amount,
		String status
	){}

	public record SearchPaymentsResponse(
		Long id,
		Long teacherId,
		Integer count,
		String nickname,
		String gender,
		Integer age,
		String profileImageUrl,
		List<String> categories,
		BigDecimal amount,
		String status,
		LocalDateTime createdAt
	){}
}
