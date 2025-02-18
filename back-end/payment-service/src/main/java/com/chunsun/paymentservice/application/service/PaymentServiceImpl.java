package com.chunsun.paymentservice.application.service;

import static com.chunsun.paymentservice.application.convert.DtoConverter.*;
import static com.chunsun.paymentservice.application.dto.FeignDto.*;
import static com.chunsun.paymentservice.application.dto.NotificationDto.*;
import static com.chunsun.paymentservice.application.dto.ServiceDto.*;
import static com.chunsun.paymentservice.common.error.PaymentErrorCodes.*;
import static com.chunsun.paymentservice.presentation.dto.ControllerDto.*;
import static java.util.function.Function.identity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.paymentservice.application.client.ClassClient;
import com.chunsun.paymentservice.application.client.CouponKafkaClient;
import com.chunsun.paymentservice.application.client.MemberClient;
import com.chunsun.paymentservice.common.exception.BusinessException;
import com.chunsun.paymentservice.domain.Order;
import com.chunsun.paymentservice.domain.OrderRepository;
import com.chunsun.paymentservice.domain.PaymentStatus;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

	private final OrderRepository orderRepository;
	private final IamportClient iamportClient;
	private final ClassClient classClient;
	private final MemberClient memberClient;
	private final CouponKafkaClient couponKafkaClient;
	private final NotificationProducerService notificationProducerService;

	// TODO 롤백 처리 구현 필요
	@Transactional
	@Override
	public ValidPaymentServiceResponse validatePayment(final ValidPaymentServiceRequest request) {
		final IamportResponse<Payment> iamportResponse = getPaymentIamportResponse(request);

		final Payment payment = iamportResponse.getResponse();
		try {
			validatePaymentResult(request, payment);
		} catch (BusinessException e) {
			cancelPayment(request);
		}

		if (request.couponId() != null) {
			couponKafkaClient.updateCouponStatus(toUpdateCouponStatusRequest(request));
		}
		UpdateRemainClassResponse updateRemainClassResponse = classClient.updateRemainClass(
			toUpdateRemainClassRequest(request));
		orderRepository.save(toOrder(request, PaymentStatus.from(payment.getStatus())));

		notificationProducerService.sendNotification(RequestDto.builder()
			.sendUserId(updateRemainClassResponse.studentId().toString())
			.targetUserId(updateRemainClassResponse.teacherId().toString())
			.type("PAYMENT")
			.message(String.format("%s번의 수업이 결제 되었습니다.", updateRemainClassResponse.remainClass()))
			.build());

		return new ValidPaymentServiceResponse(
			payment.getImpUid(),
			payment.getMerchantUid(),
			payment.getAmount(),
			payment.getStatus()
		);
	}

	private void cancelPayment(ValidPaymentServiceRequest request) {
		CancelData cancelData = new CancelData(request.impUid(), true);
		try {
			iamportClient.cancelPaymentByImpUid(cancelData);
		} catch (IamportResponseException | IOException ex) {
			log.error("결제 취소 실패: impUid={}", request.impUid(), ex);
			throw new BusinessException(PAYMENT_CANCEL_ERROR);
		}
	}

	@Override
	public Page<SearchPaymentsResponse> searchPaymentsByMemberId(final Long memberId, final Pageable pageable) {
		final Pageable sortedPageable = PageRequest.of(
			pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
		final Page<Order> orderPage = orderRepository.findAll(sortedPageable);
		final List<Order> orders = orderPage.getContent();

		if (orders.isEmpty()) {
			return new PageImpl<>(Collections.emptyList(), pageable, 0);
		}

		final List<Long> contractedClassIds = orders.stream()
			.map(Order::getContractedClassId)
			.collect(Collectors.toList());

		final List<GetTeacherIdsResponse> teacherResponses = classClient.getTeacherIds(contractedClassIds);
		final Map<Long, Long> contractedToTeacherMap = buildContractedToTeacherMap(teacherResponses);

		final List<Long> teacherIdList = teacherResponses.stream()
			.map(GetTeacherIdsResponse::teacherId)
			.distinct()
			.collect(Collectors.toList());

		final List<MemberDto> teacherInfos = memberClient.getMemberInfo(teacherIdList);
		final Map<Long, MemberDto> teacherMap = buildTeacherMap(teacherInfos);

		final List<SearchPaymentsResponse> responses = mapOrdersToResponse(orders, contractedToTeacherMap, teacherMap);

		return new PageImpl<>(responses, pageable, orderPage.getTotalElements());
	}

	private Map<Long, Long> buildContractedToTeacherMap(List<GetTeacherIdsResponse> teacherResponses) {
		return teacherResponses.stream()
			.collect(Collectors.toMap(GetTeacherIdsResponse::contractedClassId, GetTeacherIdsResponse::teacherId));
	}

	private Map<Long, MemberDto> buildTeacherMap(List<MemberDto> teacherInfos) {
		return teacherInfos.stream()
			.collect(Collectors.toMap(MemberDto::memberId, identity()));
	}

	private List<SearchPaymentsResponse> mapOrdersToResponse(List<Order> orders, Map<Long, Long> contractedToTeacherMap,
		Map<Long, MemberDto> teacherMap) {
		return orders.stream()
			.map(order -> {
				Long teacherId = contractedToTeacherMap.get(order.getContractedClassId());
				MemberDto teacherInfo = teacherMap.get(teacherId);
				return new SearchPaymentsResponse(
					order.getId(),
					teacherId,
					order.getCount(),
					teacherInfo.nickname(),
					teacherInfo.gender(),
					teacherInfo.age(),
					teacherInfo.profileImageUrl(),
					teacherInfo.categories(),
					order.getAmount(),
					order.getStatus().toString(),
					order.getCreatedAt()
				);
			})
			.collect(Collectors.toList());
	}

	private void validatePaymentResult(ValidPaymentServiceRequest request, Payment payment) {
		if (payment == null) {
			log.error("결제 정보를 찾을 수 없습니다. impUid: {}", request.impUid());
			throw new BusinessException(PAYMENT_NOT_FOUND);
		}

		if (!payment.getAmount().equals(request.amount())) {
			log.error("요청 금액: {}, 결제 금액: {}", request.amount(), payment.getAmount());
			throw new BusinessException(PAYMENT_AMOUNT_MISMATCH);
		}

		final PaymentStatus paymentStatus = PaymentStatus.from(payment.getStatus());

		if (paymentStatus != PaymentStatus.PAID) {
			log.error("결제 상태가 올바르지 않습니다. 상태: {}", payment.getStatus());
			throw new BusinessException(PAYMENT_STATUS_INVALID);
		}
	}

	private IamportResponse<Payment> getPaymentIamportResponse(ValidPaymentServiceRequest request) {
		final IamportResponse<Payment> iamportResponse;
		try {
			iamportResponse = iamportClient.paymentByImpUid(request.impUid());
		} catch (IamportResponseException | IOException e) {
			log.error(e.getMessage(), e);
			throw new BusinessException(PAYMENT_VALIDATION_ERROR);
		}
		return iamportResponse;
	}
}
