package com.chunsun.memberservice.application.dto;

import java.util.List;

public record CategoryDto() {

	public record CategoryRequest(
		List<Long> categoryIds
	){
	}

	public record CategoryResponse(
		String message,
		List<Long> categoryIds
	){
	}
}
