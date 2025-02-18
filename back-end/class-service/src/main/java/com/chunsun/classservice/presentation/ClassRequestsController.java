package com.chunsun.classservice.presentation;

import static com.chunsun.classservice.presentation.dto.ControllerDto.*;
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

import com.chunsun.classservice.application.dto.ServiceDto.SearchClassRequestsResponse;
import com.chunsun.classservice.application.service.ClassRequestService;
import com.chunsun.classservice.common.resolver.UserId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/class-requests")
@RestController
public class ClassRequestsController {

	private final ClassRequestService classRequestService;

	@UserId
	@GetMapping
	public ResponseEntity<Page<SearchClassRequestsResponse>> searchClassRequests(
		@UserId final Long memberId, @PageableDefault(size = 10) Pageable pageable) {
		return ResponseEntity.ok(classRequestService.searchClassRequests(memberId, pageable));
	}

	@UserId
	@PostMapping
	public ResponseEntity<Void> createClassRequest(
		@UserId final Long studentId, @Valid @RequestBody final ClassRequestControllerRequest request) {
		classRequestService.saveClassRequest(studentId, request.teacherId());
		return ResponseEntity.status(CREATED).build();
	}

	@UserId
	@PostMapping("/response")
	public ResponseEntity<Void> createClassRequestResponse(
		@UserId final Long teacherId, @Valid @RequestBody final ClassRequestResponseControllerRequest request) {
		boolean result = classRequestService.responseClassRequest(
			teacherId, request.studentId(), request.classRequestId(), request.status());
		if (result) {
			return ResponseEntity.status(CREATED).build();
		}else
			return ResponseEntity.status(NO_CONTENT).build();
	}
}
