package com.chunsun.chatservice.config.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chunsun.chatservice.web.dto.MemberInfoDto;

@FeignClient(name = "member-service")
public interface MemberClient {
	@GetMapping("/members/nicknames")
	List<MemberInfoDto.ResponseDto> getMembersNickname(@RequestParam List<Long> ids);
}
