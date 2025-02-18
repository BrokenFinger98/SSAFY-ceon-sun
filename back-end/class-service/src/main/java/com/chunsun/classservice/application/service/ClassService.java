package com.chunsun.classservice.application.service;

import static com.chunsun.classservice.application.dto.ServiceDto.*;
import static com.chunsun.classservice.presentation.dto.ControllerDto.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClassService {
	void saveSourceCode(final Long contractedClassId, final byte[] codeContent);

	SearchSourceCodeResponse searchSourceCode(final Long sourceCodeId);

	Page<SearchSourceCodesResponse> searchSourceCodes(final Long contractedClassId, final Pageable pageable);

	UpdateRemainClassResponse updateRemainClass(final Long contractedClassId, final Integer remainClass);

	List<GetTeacherIdsResponse> getTeacherIds(final List<Long> contractedClassIds);

	Page<SearchLessonRecordResponse> searchLessonRecords(final Long contractedClassId, final Pageable pageable);

	void updateStatus(final Long teacherId, final Long contractedClassId);
}
