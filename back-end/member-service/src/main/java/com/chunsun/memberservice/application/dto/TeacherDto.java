package com.chunsun.memberservice.application.dto;

import java.util.List;

import com.chunsun.memberservice.domain.Enum.Bank;
import com.chunsun.memberservice.domain.Entity.Category;
import com.chunsun.memberservice.domain.Enum.Gender;

public record TeacherDto() {
	public record CreateCardRequest(
		Long id,
		String description,
		String careerDescription,
		String classProgress,
		String classContents,
		Boolean isWanted,
		Bank bank,
		String account,
		Integer price) {
	}

	public record CreateCardResponse(
		String message) {
	}

	public record UpdateCardRequest(
		String description,
		String careerDescription,
		String classProgress,
		String classContents,
		Boolean isWanted,
		Bank bank,
		String account,
		Integer price) {
	}

	public record UpdateCardResponse(
		String message) {
	}

	public record GetCardResponse(
		String description,
		String careerDescription,
		String classProgress,
		String classContents,
		Boolean isWanted,
		Bank bank,
		String account,
		Integer price,
		Integer totalClassCount,
		Integer totalClassHours,
		List<Category> categories) {
	}

	public record GetDetailResponse(
		String name,
		String profileImage,
		String nickname,
		Gender gender,
		Integer age,
		String description,
		String careerDescription,
		String classProgress,
		String classContents,
		Integer price,
		Integer totalClassCount,
		Integer totalClassHours,
		List<Category> categories) {
	}

	public record ClassFinishRequest(
		Integer time
	) {
	}

	public record ClassFinishResponse(
		Integer totalClassCount,
		Integer totalClassHours
	){
	}
}
