package com.chunsun.classservice.application.client;

import static com.chunsun.classservice.application.dto.FeignDto.*;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "member-service")
public interface MemberClient {

	@GetMapping("/members/payments")
	List<MemberDto> getMemberInfo(@RequestParam final List<Long> memberIds);

	@GetMapping("/members/role")
	String getRole(@RequestParam final Long memberId);

	@PutMapping("/teachers/class/{id}")
	ClassFinishResponse classCountUpdate(@PathVariable final Long id,
		@RequestBody final ClassFinishRequest request);
}
