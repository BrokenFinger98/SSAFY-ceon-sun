package com.chunsun.paymentservice.presentation;

import static com.chunsun.paymentservice.application.convert.DtoConverter.*;
import static com.chunsun.paymentservice.application.dto.ServiceDto.*;
import static com.chunsun.paymentservice.presentation.dto.ControllerDto.*;
import static org.springframework.http.HttpStatus.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.paymentservice.application.service.PaymentService;
import com.chunsun.paymentservice.common.resolver.UserId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

	private final PaymentService paymentService;

	@UserId
	@PostMapping
	public ResponseEntity<ValidPaymentControllerResponse> validatePayment(
		@UserId final Long memberId, @Valid @RequestBody final ValidPaymentControllerRequest request) {
		final ValidPaymentServiceResponse response = paymentService.validatePayment(
			toValidPaymentServiceRequest(memberId, request));
		return ResponseEntity.status(CREATED).body(toValidPaymentControllerResponse(response));
	}

	@UserId
	@GetMapping
	public ResponseEntity<Page<SearchPaymentsResponse>> searchPayments(
		@UserId final Long memberId, @PageableDefault(size = 10) Pageable pageable){
		return ResponseEntity.ok(paymentService.searchPaymentsByMemberId(memberId, pageable));
	}
}
