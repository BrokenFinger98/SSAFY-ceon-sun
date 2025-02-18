package com.chunsun.classservice.application.dto;

import java.util.List;

public record FeignDto() {
	public record MemberDto(
		Long memberId,
		String nickname,
		String gender,
		Integer age,
		String profileImageUrl,
		List<String> categories
	){}

	public record ClassFinishRequest(
		Integer time
	){}

	public record ClassFinishResponse(
		Integer time
	){}
}
