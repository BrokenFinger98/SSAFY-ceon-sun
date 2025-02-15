package com.chunsun.chatservice.web.dto;

import lombok.Builder;

public record MemberInfoDto() {
	@Builder
	public record ResponseDto(
		Long id,
		String nickname,
		String profileImage
	) {
	}
}
