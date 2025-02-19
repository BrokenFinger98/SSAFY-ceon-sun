package com.chunsun.classservice.application.service;

import static com.chunsun.classservice.application.dto.FeignDto.*;
import static com.chunsun.classservice.application.dto.NotificationDto.*;
import static com.chunsun.classservice.application.dto.ServiceDto.*;
import static com.chunsun.classservice.common.error.ClassErrorCodes.DUPLICATE_CLASS_REQUEST;
import static com.chunsun.classservice.common.error.ClassErrorCodes.INVALID_AUTHORITY;
import static com.chunsun.classservice.common.error.ClassErrorCodes.INVALID_REQUEST;
import static java.util.function.UnaryOperator.identity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.classservice.application.client.MemberClient;
import com.chunsun.classservice.common.exception.BusinessException;
import com.chunsun.classservice.domain.ClassRequest;
import com.chunsun.classservice.domain.ClassRequestRepository;
import com.chunsun.classservice.domain.ContractedClass;
import com.chunsun.classservice.domain.ContractedClassRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ClassRequestServiceImpl implements ClassRequestService {

	private final NotificationProducerService notificationProducerService;
	private final ClassRequestRepository classRequestRepository;
	private final ContractedClassRepository contractedClassRepository;
	private final MemberClient memberClient;

	@Override
	public Page<SearchClassRequestsResponse> searchClassRequests(final Long memberId, final Pageable pageable) {
		final Pageable sortedPageable = getSortedPageable(pageable);
		final String role = memberClient.getRole(memberId);

		if ("teacher".equalsIgnoreCase(role)) {
			final Page<ClassRequest> page = classRequestRepository.findAllByTeacherId(memberId, sortedPageable);
			return buildResponse(page, ClassRequest::getStudentId, sortedPageable);
		} else if ("student".equalsIgnoreCase(role)) {
			final Page<ClassRequest> page = classRequestRepository.findAllByStudentId(memberId, sortedPageable);
			return buildResponse(page, ClassRequest::getTeacherId, sortedPageable);
		} else {
			throw new BusinessException(INVALID_AUTHORITY);
		}
	}

	@Transactional
	@Override
	public void saveClassRequest(final Long studentId, final Long teacherId) {
		classRequestRepository.findByTeacherIdAndStudentId(teacherId, studentId)
			.ifPresent(cr -> {
				throw new BusinessException(DUPLICATE_CLASS_REQUEST);
			});
		final ClassRequest classRequest = new ClassRequest(teacherId, studentId);
		classRequestRepository.save(classRequest);

		final Map<Long, MemberDto> memberMap = memberClient.getMemberInfo(
				List.of(classRequest.getTeacherId(), classRequest.getStudentId()))
			.stream()
			.collect(Collectors.toMap(MemberDto::memberId, Function.identity()));

		final MemberDto teacherInfo = memberMap.get(classRequest.getTeacherId());
		final MemberDto studentInfo = memberMap.get(classRequest.getStudentId());

		notificationProducerService.sendNotification(
			RequestDto.builder()
				.sendUserId(classRequest.getStudentId().toString())
				.targetUserId(classRequest.getTeacherId().toString())
				.type("CLASS")
				.message(String.format("%s 회원님이 %s 회원님에게 과외를 신청하셨습니다.",
					teacherInfo.nickname(), studentInfo.nickname()))
				.build());
	}

	@Transactional
	@Override
	public boolean responseClassRequest(
		final Long teacherId, final Long studentId, final Long classRequestId, final String status) {

		classRequestRepository.deleteByIdAndTeacherIdAndStudentId(classRequestId, teacherId, studentId)
			.orElseThrow(() -> new BusinessException(INVALID_REQUEST));

		final Map<Long, MemberDto> memberMap = memberClient.getMemberInfo(
				List.of(teacherId, studentId))
			.stream()
			.collect(Collectors.toMap(MemberDto::memberId, Function.identity()));

		final MemberDto teacherInfo = memberMap.get(teacherId);
		final MemberDto studentInfo = memberMap.get(studentId);

		if ("accept".equalsIgnoreCase(status)) {
			contractedClassRepository.save(
				new ContractedClass(teacherId, studentId));
			final String statusMessage = "수락";
			sendNotification(teacherInfo, studentInfo, statusMessage);
			return true;
		} else if ("reject".equalsIgnoreCase(status)) {
			final String statusMessage = "거절";
			sendNotification(teacherInfo, studentInfo, statusMessage);
			return false;
		} else {
			throw new BusinessException(INVALID_REQUEST);
		}
	}

	private void sendNotification(
		final MemberDto teacherInfo,
		final MemberDto studentInfo,
		final String status) {

		notificationProducerService.sendNotification(
			RequestDto.builder()
				.sendUserId(teacherInfo.memberId().toString())
				.targetUserId(studentInfo.memberId().toString())
				.type("CLASS")
				.message(String.format("%s 선생님이 %s 회원님의 과외 신청을 %s하셨습니다.",
					teacherInfo.nickname(), studentInfo.nickname(), status))
				.build());
	}

	private Page<SearchClassRequestsResponse> buildResponse(final Page<ClassRequest> classRequestPage,
		final Function<ClassRequest, Long> foreignKeyExtractor, final Pageable sortedPageable) {

		final List<ClassRequest> classRequestList = classRequestPage.getContent();
		if (classRequestList.isEmpty()) {
			return new PageImpl<>(Collections.emptyList(), sortedPageable, 0);
		}

		final List<Long> foreignIds = classRequestList.stream()
			.map(foreignKeyExtractor)
			.distinct()
			.collect(Collectors.toList());

		final List<MemberDto> memberInfos = memberClient.getMemberInfo(foreignIds);
		final Map<Long, MemberDto> memberMap = memberInfos.stream()
			.collect(Collectors.toMap(MemberDto::memberId, identity()));

		final List<SearchClassRequestsResponse> responses = classRequestList.stream()
			.map(cr -> {
				Long foreignId = foreignKeyExtractor.apply(cr);
				MemberDto member = memberMap.get(foreignId);
				return new SearchClassRequestsResponse(
					cr.getId(),
					foreignId,
					member.nickname(),
					member.gender(),
					member.age(),
					member.profileImageUrl(),
					member.categories(),
					cr.getCreatedAt()
				);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(responses, sortedPageable, classRequestPage.getTotalElements());
	}

	private static Pageable getSortedPageable(Pageable pageable) {
		return PageRequest.of(
			pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
	}
}
