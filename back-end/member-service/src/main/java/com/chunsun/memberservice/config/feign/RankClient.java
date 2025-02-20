package com.chunsun.memberservice.config.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.chunsun.memberservice.application.dto.MemberDto;

@FeignClient(name = "rank-service")
public interface RankClient {

	@PostMapping("/ranking/{teacherId}/view")
	String incrementTeacherViewCount(@PathVariable("teacherId") Long teacherId);

	@GetMapping("/ranking/top")
	List<MemberDto.TeacherTupleDto> getTeachersRank();

}
