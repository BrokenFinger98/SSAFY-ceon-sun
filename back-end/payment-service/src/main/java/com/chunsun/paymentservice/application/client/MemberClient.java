package com.chunsun.paymentservice.application.client;

import static com.chunsun.paymentservice.application.dto.FeignDto.*;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "member-service")
public interface MemberClient {

	@GetMapping("/payments")
	List<MemberDto> getMemberInfo(@RequestParam final List<Long> memberIds);
}
