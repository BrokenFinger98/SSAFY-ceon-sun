package com.chunsun.rankservice.application.dto;

public record RankingDto() {
	public record TeacherTupleDto(
		String value,
		Double score
	){
	}
}
