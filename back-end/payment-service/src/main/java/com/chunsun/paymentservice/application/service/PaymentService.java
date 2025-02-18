package com.chunsun.paymentservice.application.service;

import static com.chunsun.paymentservice.application.dto.ServiceDto.*;
import static com.chunsun.paymentservice.presentation.dto.ControllerDto.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
	ValidPaymentServiceResponse validatePayment(final ValidPaymentServiceRequest validPaymentServiceRequest);

	Page<SearchPaymentsResponse> searchPaymentsByMemberId(final Long memberId, final Pageable pageable);
}
