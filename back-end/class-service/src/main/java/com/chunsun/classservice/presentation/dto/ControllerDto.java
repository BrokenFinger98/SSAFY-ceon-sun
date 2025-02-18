package com.chunsun.classservice.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record ControllerDto() {
	public record UploadSourceCodeControllerRequest(
		@NotNull(message = "contractedClassId가 없습니다.")
		Long contractedClassId,

		@NotNull(message = "아무 내용도 없습니다.")
		String sourceCode
	) {
	}

	public record DownloadSourceCodeControllerRequest(
		@NotNull(message = "sourceCodeId가 없습니다.")
		Long sourceCodeId
	) {
	}

	public record ClassRequestControllerRequest(
		@NotNull(message = "teacherId가 없습니다.")
		Long teacherId
	){}

	public record ClassRequestResponseControllerRequest(
		@NotNull(message = "studentId가 없습니다.")
		Long studentId,

		@NotNull(message = "classRequestId가 없습니다.")
		Long classRequestId,

		@NotNull(message = "status가 없습니다.")
		String status
	){}

	public record UpdateRemainClassRequest(
		@NotNull(message = "contractedClassId가 없습니다.")
		Long contractedClassId,

		@NotNull(message = "remainClass가 없습니다.")
		Integer remainClass
	) {
	}

	public record UpdateRemainClassResponse(
		Long studentId,
		Long teacherId,
		Integer remainClass
	){}

	public record GetTeacherIdsResponse(
		Long contractedClassId,
		Long teacherId
	){}

	public record GetSessionResponse(
		String token
	){}
}
