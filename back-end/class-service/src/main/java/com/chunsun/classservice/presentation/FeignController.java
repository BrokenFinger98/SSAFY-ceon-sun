package com.chunsun.classservice.presentation;

import static com.chunsun.classservice.presentation.dto.ControllerDto.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.classservice.application.service.ClassService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping
@RestController
public class FeignController {

	private final ClassService classService;

	@PatchMapping("/contracted-class")
	public ResponseEntity<UpdateRemainClassResponse> updateRemainClass(
		@Valid @RequestBody final UpdateRemainClassRequest request) {
		return ResponseEntity.ok(classService.updateRemainClass(request.contractedClassId(), request.remainClass()));
	}

	@GetMapping("/members")
	public ResponseEntity<List<GetTeacherIdsResponse>> getTeacherIds(
		@RequestParam final List<Long> contractedClassIds) {
		return ResponseEntity.ok(classService.getTeacherIds(contractedClassIds));
	}
}
