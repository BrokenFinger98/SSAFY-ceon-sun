package com.chunsun.chatservice.web.dto;

import lombok.Builder;

public record ChatRoomDto() {
	@Builder
	public record ResponseDto(
		Long id,
		Long partnerId,
		String partnerName,
		String profileImage
	) {
	}
}
