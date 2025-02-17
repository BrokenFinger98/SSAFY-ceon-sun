package com.chunsun.notificationservice.application.dto.student;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StudentDto {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GetListResponse {
		private List<Long> studentsId;
	}
}
