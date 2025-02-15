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

import com.chunsun.memberservice.application.dto.TeacherDto;
import com.chunsun.memberservice.application.service.TeacherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

	private final TeacherService teacherService;

	/*
	* 선생 카드 생성
	* 선생 카드 수정
	* 선생 카드 조회(본인 카드)
	* 선생 카드 조회(남 카드)
	* 선생 수업 횟수/시간 업데이트
	* 선생 수업 횟수/시간 조회
	* */

	@PostMapping
	public ResponseEntity<TeacherDto.CreateCardResponse> addCard(
		@RequestBody TeacherDto.CreateCardRequest request) {

		TeacherDto.CreateCardResponse response = teacherService.createCard(request);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<TeacherDto.UpdateCardResponse> updateCard(
		@PathVariable Long id,
		@RequestBody TeacherDto.UpdateCardRequest request) {

		TeacherDto.UpdateCardResponse response = teacherService.updateCard(id, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<TeacherDto.GetCardResponse> getCard(
		@PathVariable Long id) {

		TeacherDto.GetCardResponse response = teacherService.getCard(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/details/{id}")
	public ResponseEntity<TeacherDto.GetDetailResponse> getDetail(
		@PathVariable Long id) {

		TeacherDto.GetDetailResponse response = teacherService.getDetail(id);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/class/{id}")
	public ResponseEntity<TeacherDto.ClassFinishResponse> classCountUpdate(
		@PathVariable Long id,
		@RequestBody TeacherDto.ClassFinishRequest request){

		TeacherDto.ClassFinishResponse response = teacherService.updateClass(id, request);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/class/{id}")
	public ResponseEntity<TeacherDto.ClassFinishResponse> getClassCount(
		@PathVariable Long id) {

		TeacherDto.ClassFinishResponse response = teacherService.getClass(id);

		return ResponseEntity.ok(response);
	}
}
