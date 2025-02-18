package com.chunsun.paymentservice.application.convert;

import static com.chunsun.paymentservice.application.dto.FeignDto.*;
import static com.chunsun.paymentservice.application.dto.ServiceDto.*;
import static com.chunsun.paymentservice.presentation.dto.ControllerDto.*;

import com.chunsun.paymentservice.domain.Order;
import com.chunsun.paymentservice.domain.PaymentStatus;

public class DtoConverter {

	public static ValidPaymentServiceRequest toValidPaymentServiceRequest(
		Long memberId, ValidPaymentControllerRequest request) {
		return new ValidPaymentServiceRequest(
			memberId, request.impUid(), request.merchantUid(), request.amount(), request.couponId(), request.count(),
			request.contractedClassId());
	}

	public static ValidPaymentControllerResponse toValidPaymentControllerResponse(
		ValidPaymentServiceResponse response) {
		return new ValidPaymentControllerResponse(
			response.impUid(), response.merchantUid(), response.amount(), response.status());
	}

	public static UpdateCouponStatusRequest toUpdateCouponStatusRequest(ValidPaymentServiceRequest request) {
		return new UpdateCouponStatusRequest(request.memberId(), request.couponId());
	}

	public static UpdateRemainClassRequest toUpdateRemainClassRequest(ValidPaymentServiceRequest request) {
		return new UpdateRemainClassRequest(request.contractedClassId(), request.count());
	}

	public static Order toOrder(ValidPaymentServiceRequest request, PaymentStatus status) {
		return new Order(request.memberId(), request.couponId(), request.contractedClassId(), request.count(),
			request.amount(), request.merchantUid(), request.impUid(), status);
	}
}
