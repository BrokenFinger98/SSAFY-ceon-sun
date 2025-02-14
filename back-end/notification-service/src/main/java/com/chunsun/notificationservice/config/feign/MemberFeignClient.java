package com.chunsun.notificationservice.config.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.chunsun.notificationservice.application.dto.student.StudentDto;

@FeignClient(name = "member-service")
public interface MemberFeignClient {

	@GetMapping("/students")
	StudentDto.GetListResponse getStudentIds();
}
