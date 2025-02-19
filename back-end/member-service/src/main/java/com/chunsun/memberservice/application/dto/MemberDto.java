package com.chunsun.memberservice.application.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.chunsun.memberservice.application.dto.CategoryDto.CategoryName;
import com.chunsun.memberservice.domain.Entity.Category;
import com.chunsun.memberservice.domain.Enum.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record MemberDto() {

	public record SignUpRequest(
		@NotNull(message = "kakaoId가 없습니다.")
		String kakaoId,
		@Email(message = "Email 형식이 아닙니다.")
		String email,
		@NotNull(message = "name이 없습니다.")
		String name,
		@NotNull(message = "nickname이 없습니다.")
		String nickname,
		@NotNull(message = "birthdate이 없습니다.")
		LocalDate birthdate,
		@NotNull(message = "gender가 없습니다.")
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

	public record GetInfoResponse(
		String name,
		String nickname,
		String email,
		LocalDate birthdate,
		Gender gender,
		String profileImage
	) {
	}

	public record SearchMemberResponse(
		Long memberId,
		String profileImage,
		String nickname,
		Integer age,
		Gender gender,
		List<CategoryName> categories){
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
