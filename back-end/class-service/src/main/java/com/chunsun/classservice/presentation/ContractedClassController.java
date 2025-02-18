package com.chunsun.classservice.presentation;

import static com.chunsun.classservice.application.dto.ServiceDto.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.classservice.application.service.ContractedClassService;
import com.chunsun.classservice.common.resolver.UserId;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/contracted-class")
@RestController
public class ContractedClassController {

	private final ContractedClassService contractedClassService;

	@UserId
	@GetMapping
	public ResponseEntity<Page<SearchContractedClassResponse>> searchContractedClass(
		@UserId final Long memberId, @PageableDefault(size = 10) Pageable pageable) {
		return ResponseEntity.ok(contractedClassService.searchContractedClass(memberId, pageable));
	}
}
