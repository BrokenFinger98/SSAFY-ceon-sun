package com.chunsun.memberservice.presentation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.memberservice.application.dto.StudentDto;
import com.chunsun.memberservice.application.service.StudentService;
import com.chunsun.memberservice.domain.Entity.Student;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

	private final StudentService studentService;

	/*
	* 학생 카드 생성
	* 학생 카드 수정
	* 학생 카드 조회(본인 카드)
	* 학생 카드 조회(남 카드)
	* */

	@PostMapping
	public ResponseEntity<StudentDto.CardResponse> addCard(
		@RequestBody StudentDto.CreateCardRequest request) {

		StudentDto.CardResponse response = studentService.createCard(request);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<StudentDto.CardResponse> updateCard(
		@PathVariable Long id,
		@RequestBody StudentDto.UpdateCardRequest request) {

		StudentDto.CardResponse response = studentService.updateCard(id, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<StudentDto.GetCardResponse> getCard(
		@PathVariable Long id) {

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
	public ResponseEntity<StudentDto.GetListResponse> studentsId() {
		StudentDto.GetListResponse response = studentService.getList();

		return ResponseEntity.ok(response);
	}
}
