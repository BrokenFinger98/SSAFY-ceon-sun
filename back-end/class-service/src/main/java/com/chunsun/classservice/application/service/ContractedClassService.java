package com.chunsun.classservice.application.service;

import static com.chunsun.classservice.application.dto.ServiceDto.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContractedClassService {
	Page<SearchContractedClassResponse> searchContractedClass(final Long memberId, final Pageable pageable);
}
