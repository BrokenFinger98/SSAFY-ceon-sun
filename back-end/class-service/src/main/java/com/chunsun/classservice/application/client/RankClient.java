package com.chunsun.classservice.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "rank-service")
public interface RankClient {

	@PostMapping("/ranking/{teacherId}/class")
	String incrementTeacherClassCount(@PathVariable Long teacherId);
}
