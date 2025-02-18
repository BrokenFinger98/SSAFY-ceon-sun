package com.chunsun.classservice.application.service;

import static com.chunsun.classservice.application.dto.ServiceDto.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClassRequestService {
	Page<SearchClassRequestsResponse> searchClassRequests(final Long memberId, final Pageable pageable);

	void saveClassRequest(final Long studentId, final Long teacherId);

	boolean responseClassRequest(
		final Long teacherId, final Long studentId, final Long classRequestId, final String status);
}
