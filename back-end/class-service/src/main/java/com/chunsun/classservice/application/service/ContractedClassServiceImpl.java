package com.chunsun.classservice.application.service;

import static com.chunsun.classservice.application.dto.FeignDto.*;
import static com.chunsun.classservice.application.dto.ServiceDto.*;
import static com.chunsun.classservice.common.error.ClassErrorCodes.INVALID_AUTHORITY;
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
import com.chunsun.classservice.domain.ContractedClass;
import com.chunsun.classservice.domain.ContractedClassRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ContractedClassServiceImpl implements ContractedClassService {

	private final ContractedClassRepository contractedClassRepository;
	private final MemberClient memberClient;

	@Override
	public Page<SearchContractedClassResponse> searchContractedClass(final Long memberId, final Pageable pageable) {
		final Pageable sortedPageable = getSortedPageable(pageable);
		final String role = memberClient.getRole(memberId);

		if ("teacher".equalsIgnoreCase(role)) {
			final Page<ContractedClass> page = contractedClassRepository.findAllByTeacherId(memberId, sortedPageable);
			return buildResponse(page, ContractedClass::getStudentId, sortedPageable);
		} else if ("student".equalsIgnoreCase(role)) {
			final Page<ContractedClass> page = contractedClassRepository.findAllByStudentId(memberId, sortedPageable);
			return buildResponse(page, ContractedClass::getTeacherId, sortedPageable);
		} else {
			throw new BusinessException(INVALID_AUTHORITY);
		}
	}

	private Page<SearchContractedClassResponse> buildResponse(final Page<ContractedClass> contractedPage,
		final Function<ContractedClass, Long> foreignKeyExtractor, final Pageable sortedPageable) {

		final List<ContractedClass> contractedList = contractedPage.getContent();
		if (contractedList.isEmpty()) {
			return new PageImpl<>(Collections.emptyList(), sortedPageable, 0);
		}

		final List<Long> foreignIds = contractedList.stream()
			.map(foreignKeyExtractor)
			.distinct()
			.collect(Collectors.toList());

		final List<MemberDto> memberInfos = memberClient.getMemberInfo(foreignIds);
		final Map<Long, MemberDto> memberMap = memberInfos.stream()
			.collect(Collectors.toMap(MemberDto::memberId, identity()));

		final List<SearchContractedClassResponse> responses = contractedList.stream()
			.map(cc -> {
				Long foreignId = foreignKeyExtractor.apply(cc);
				MemberDto member = memberMap.get(foreignId);
				return new SearchContractedClassResponse(
					cc.getId(),
					foreignId,
					member.nickname(),
					member.gender(),
					member.age(),
					member.profileImageUrl(),
					member.categories(),
					cc.getCreatedAt(),
					cc.getRemainClass(),
					cc.getStatus().toString()
				);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(responses, sortedPageable, contractedPage.getTotalElements());
	}

	private static Pageable getSortedPageable(Pageable pageable) {
		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
	}
}

