package com.chunsun.paymentservice.application.dto;

import java.util.List;

public record FeignDto() {

	public record UpdateCouponStatusRequest(
		Long memberId,
		Long couponId
	) {
	}

	public record UpdateRemainClassRequest(
		Long contractedClassId,
		Integer remainClass
	) {
	}

	public record UpdateRemainClassResponse(
		Long studentId,
		Long teacherId,
		Integer remainClass
	){}

	public record MemberDto(
		Long memberId,
		String nickname,
		String gender,
		Integer age,
		String profileImageUrl,
		List<String> categories
	){}

	public record GetTeacherIdsResponse(
		Long contractedClassId,
		Long teacherId
	){}
}
