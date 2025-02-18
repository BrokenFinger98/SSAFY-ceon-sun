package com.chunsun.paymentservice.domain;

import static com.chunsun.paymentservice.common.error.PaymentErrorCodes.*;

import com.chunsun.paymentservice.common.exception.BusinessException;

public enum PaymentStatus {
	PAID,
	CANCELLED;

	public static PaymentStatus from(String status) {
		if (status == null) {
			throw new BusinessException(PAYMENT_STATUS_INVALID);
		}
		if ("paid".equalsIgnoreCase(status)) {
			return PAID;
		} else if ("cancelled".equalsIgnoreCase(status)) {
			return CANCELLED;
		} else {
			throw new BusinessException(PAYMENT_STATUS_INVALID);
		}
	}
}
