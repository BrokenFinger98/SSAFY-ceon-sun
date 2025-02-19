package com.chunsun.paymentservice.application.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.chunsun.paymentservice.application.dto.FeignDto.*;

@FeignClient(name = "class-service")
public interface ClassClient {

	@PutMapping("/contracted-class")
	UpdateRemainClassResponse updateRemainClass(@RequestBody final UpdateRemainClassRequest request);

	@GetMapping("/members")
	List<GetTeacherIdsResponse> getTeacherIds(@RequestParam final List<Long> contractedClassIds);
}
