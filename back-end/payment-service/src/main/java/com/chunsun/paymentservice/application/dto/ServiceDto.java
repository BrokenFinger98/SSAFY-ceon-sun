package com.chunsun.paymentservice.application.dto;

import java.math.BigDecimal;

public record ServiceDto() {

	public record ValidPaymentServiceRequest(
		Long memberId,
		String impUid,
		String merchantUid,
		BigDecimal amount,
		Long couponId,
		Integer count,
		Long contractedClassId
	) {
	}

	public record ValidPaymentServiceResponse(
		String impUid,
		String merchantUid,
		BigDecimal amount,
		String status
	) {
	}
}
