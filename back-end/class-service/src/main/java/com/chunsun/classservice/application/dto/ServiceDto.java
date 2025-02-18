package com.chunsun.classservice.application.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Column;

public record ServiceDto() {
	public record SearchSourceCodeResponse(
		Long id,
		byte[] codeContent,
		LocalDateTime createdAt
	) {
	}

	public record SearchSourceCodesResponse(
		Long id,
		String fileName,
		LocalDateTime createdAt
	) {
	}

	public record SearchClassRequestsResponse(
		Long id,
		Long memberId,
		String nickname,
		String gender,
		Integer age,
		String profileImageUrl,
		List<String> categories,
		LocalDateTime createdAt
	) {
	}

	public record SearchContractedClassResponse(
		Long id,
		Long memberId,
		String nickname,
		String gender,
		Integer age,
		String profileImageUrl,
		List<String> categories,
		LocalDateTime createdAt,
		Integer count,
		String status
	) {
	}

	public record SearchLessonRecordResponse(
		Long id,
		LocalTime lessonTime,
		LocalDateTime createdAt
	) {
	}
}
