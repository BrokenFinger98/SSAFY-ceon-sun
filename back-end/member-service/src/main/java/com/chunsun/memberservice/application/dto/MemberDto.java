package com.chunsun.memberservice.application.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.chunsun.memberservice.domain.Entity.Category;
import com.chunsun.memberservice.domain.Enum.Gender;

public record MemberDto() {

	public record SignUpRequest(
		String kakaoId,
		String email,
		String name,
		String nickname,
		LocalDate birthdate,
		Gender gender) {
	}

	public record SignUpResponse(
		String message) {
	}

	public record UpdateInfoRequest(
		Long id,
		String nickname,
		MultipartFile profileImage
	) {
	}

	public record UpdateInfoResponse() {
	}

	public record GetInfoResponse(
		String name,
		String nickname,
		String email,
		LocalDate birthdate,
		Gender gender,
		String profileImage
	) {
	}

	public record MemberListItem(
		Long id,
		String profileImage,
		String nickname,
		Integer age,
		Gender gender,
		List<Category> categories){
	}

	public record TeacherTupleDto(
		String value,
		Double score
	){
	}

	public record TeacherListItem(
		Long id,
		String profileImage,
		String nickname,
		Integer age,
		Gender gender,
		List<Category> categories,
		Double score){
	}

	public record MemberNickNameDto(
		Long id,
		String nickname,
		String profileImage
	){
	}

	public record MemberPaymentDto(
		Long memberId,
		String nickname,
		String gender,
		Integer age,
		String profileImageUrl,
		List<String> categories
	){
	}

}
