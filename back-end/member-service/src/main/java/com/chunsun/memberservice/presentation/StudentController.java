package com.chunsun.memberservice.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.memberservice.application.dto.StudentDto;
import com.chunsun.memberservice.application.service.StudentService;
import com.chunsun.memberservice.common.util.HeaderUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

	private final StudentService studentService;

	@PostMapping
	public ResponseEntity<StudentDto.CardResponse> addCard(
		@RequestHeader("X-User-ID") Long memberId,
		@RequestBody StudentDto.CreateCardRequest request) {

		StudentDto.CardResponse response = studentService.createCard(memberId, request);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<StudentDto.CardResponse> updateCard(
		@RequestHeader("X-User-ID") Long memberId,
		@PathVariable Long id,
		@RequestBody StudentDto.UpdateCardRequest request) {
		HeaderUtil.validateUserId(id, memberId);

		StudentDto.CardResponse response = studentService.updateCard(id, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<StudentDto.GetCardResponse> getCard(
		@RequestHeader("X-User-ID") Long memberId,
		@PathVariable Long id) {
		HeaderUtil.validateUserId(id, memberId);

		StudentDto.GetCardResponse response = studentService.getCard(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/details/{id}")
	public ResponseEntity<StudentDto.GetDetailResponse> getDetail(
		@PathVariable Long id) {

		StudentDto.GetDetailResponse response = studentService.getDetail(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<StudentDto.GetListResponse> getStudentsId() {
		StudentDto.GetListResponse response = studentService.getList();

		return ResponseEntity.ok(response);
	}
}
