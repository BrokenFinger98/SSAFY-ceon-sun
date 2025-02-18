package com.chunsun.classservice.application.service;

import static com.chunsun.classservice.application.dto.ServiceDto.*;
import static com.chunsun.classservice.common.error.ClassErrorCodes.*;
import static com.chunsun.classservice.presentation.dto.ControllerDto.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.classservice.common.exception.BusinessException;
import com.chunsun.classservice.domain.ContractedClass;
import com.chunsun.classservice.domain.ContractedClassRepository;
import com.chunsun.classservice.domain.LessonRecord;
import com.chunsun.classservice.domain.LessonRecordRepository;
import com.chunsun.classservice.domain.SourceCode;
import com.chunsun.classservice.domain.SourceCodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ClassServiceImpl implements ClassService {

	private final ContractedClassRepository contractedClassRepository;
	private final SourceCodeRepository sourceCodeRepository;
	private final LessonRecordRepository lessonRecordRepository;

	@Transactional
	@Override
	public void saveSourceCode(final Long contractedClassId, final byte[] codeContent) {
		final ContractedClass contractedClass = contractedClassRepository.findById(contractedClassId)
			.orElseThrow(() -> new BusinessException(CONTRACTED_CLASS_NOT_FOUND));
		final SourceCode sourceCode = new SourceCode(contractedClass, codeContent);
		sourceCodeRepository.save(sourceCode);
	}

	@Override
	public SearchSourceCodeResponse searchSourceCode(final Long sourceCodeId) {
		final SourceCode sourceCode = sourceCodeRepository.findById(sourceCodeId)
			.orElseThrow(() -> new BusinessException(SOURCE_CODE_NOT_FOUND));
		return new SearchSourceCodeResponse(sourceCode.getId(), sourceCode.getCodeContent(), sourceCode.getCreatedAt());
	}

	@Override
	public Page<SearchSourceCodesResponse> searchSourceCodes(final Long contractedClassId, final Pageable pageable) {
		final Pageable sortedPageable = getSortedPageable(pageable);
		final Page<SourceCode> sourceCodePage =
			sourceCodeRepository.findAllByContractedClassId(contractedClassId, sortedPageable);

		final List<SearchSourceCodesResponse> responses = sourceCodePage.getContent().stream()
			.map(sourceCode -> new SearchSourceCodesResponse(
				sourceCode.getId(),
				String.format("source_code_%s_%s", sourceCode.getId(), sourceCode.getCreatedAt()),
				sourceCode.getCreatedAt()
			))
			.collect(Collectors.toList());

		return new PageImpl<>(responses, pageable, sourceCodePage.getTotalElements());
	}

	@Transactional
	@Override
	public UpdateRemainClassResponse updateRemainClass(final Long contractedClassId, final Integer remainClass) {
		final ContractedClass contractedClass = contractedClassRepository.findById(contractedClassId)
			.orElseThrow(() -> new BusinessException(CONTRACTED_CLASS_NOT_FOUND));

		contractedClass.updateRemainClass(remainClass);

		return new UpdateRemainClassResponse(
			contractedClass.getStudentId(), contractedClass.getTeacherId(), contractedClass.getRemainClass());
	}

	@Override
	public List<GetTeacherIdsResponse> getTeacherIds(final List<Long> contractedClassIds) {
		return contractedClassRepository.findAllById(contractedClassIds)
			.stream()
			.map(cc -> new GetTeacherIdsResponse(cc.getId(), cc.getTeacherId()))
			.toList();
	}

	@Override
	public Page<SearchLessonRecordResponse> searchLessonRecords(Long contractedClassId, Pageable pageable) {
		final ContractedClass contractedClass = contractedClassRepository.findById(contractedClassId)
			.orElseThrow(() -> new BusinessException(CONTRACTED_CLASS_NOT_FOUND));
		final Pageable sortedPageable = getSortedPageable(pageable);
		final Page<LessonRecord> lessonRecordPage = lessonRecordRepository.findAllByContractedClass(
			contractedClass, sortedPageable);

		final List<SearchLessonRecordResponse> responses = lessonRecordPage.getContent()
			.stream()
			.map(lessonRecord -> new SearchLessonRecordResponse(
				lessonRecord.getId(),
				lessonRecord.getLessonTime(),
				lessonRecord.getUpdatedAt()))
			.toList();

		return new PageImpl<>(responses, pageable, lessonRecordPage.getTotalElements());
	}

	@Transactional
	@Override
	public void updateStatus(final Long teacherId, final Long contractedClassId) {
		final ContractedClass contractedClass = contractedClassRepository.findById(contractedClassId)
			.orElseThrow(() -> new BusinessException(CONTRACTED_CLASS_NOT_FOUND));
		if(!contractedClass.getTeacherId().equals(teacherId)) {
			throw new BusinessException(INVALID_AUTHORITY);
		}
		contractedClass.endContractedClass();
	}

	private static Pageable getSortedPageable(final Pageable pageable) {
		return PageRequest.of(
			pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
	}
}
