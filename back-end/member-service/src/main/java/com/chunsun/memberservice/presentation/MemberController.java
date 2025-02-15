package com.chunsun.memberservice.presentation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.memberservice.application.dto.MemberDto;
import com.chunsun.memberservice.application.service.MemberService;
import com.chunsun.memberservice.config.feign.RankClient;
import com.chunsun.memberservice.domain.Repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

	private final MemberService memberService;
	private final MemberRepository memberRepository;
	private final RankClient rankClient;

	/*
	* 회원가입
	* 회원정보 수정
	* 회원정보 조회(본인 상세정보)
	* 회원 탈퇴
	* 닉네임 중복 체크
	* 학생 또는 선생 검색
	* 회원 존재 유무 확인
	* */

	@PostMapping
	public ResponseEntity<MemberDto.SignUpResponse> createInfo(
		@RequestBody MemberDto.SignUpRequest request) {

		MemberDto.SignUpResponse response = memberService.signUp(request);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<MemberDto.UpdateInfoResponse> updateInfo(
		@PathVariable Long id,
		@RequestBody MemberDto.UpdateInfoRequest request) {

		MemberDto.UpdateInfoResponse updateInfo = memberService.updateMemberInfo(id, request);

		return ResponseEntity.ok(updateInfo);
	}

	@GetMapping("/{id}")
	public ResponseEntity<MemberDto.GetInfoResponse> getInfo(
		@PathVariable Long id) {

		MemberDto.GetInfoResponse getInfo = memberService.getMemberInfo(id);

		return ResponseEntity.ok(getInfo);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> withdraw(
		@PathVariable Long id) {

		memberService.deleteMember(id);

		return ResponseEntity.ok().build();
	}

	@GetMapping()
	public ResponseEntity<Void> nicknameCheck(
		@RequestParam String nickname) {

		memberService.checkNicknameAvailability(nickname);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}/search")
	public ResponseEntity<Page<MemberDto.MemberListItem>> searchMembers(
		@PathVariable Long id,
		@RequestParam(required = false) String category,
		@RequestParam(required = false) String gender,
		@RequestParam(required = false) String age,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {

		Page<MemberDto.MemberListItem> result = memberService.getFilterMembers(category, gender, age, page, size, id);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/exist/{id}")
	public boolean isExist(
		@PathVariable Long id) {
		return memberRepository.existsById(id);
	}

	@GetMapping("/ranking")
	public ResponseEntity<List<MemberDto.TeacherListItem>> getTeachersRank(){

		List<MemberDto.TeacherTupleDto> teachersRank = rankClient.getTeachersRank();

		List<MemberDto.TeacherListItem> result = memberService.getTeachersRank(teachersRank);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/nicknames")
	public List<MemberDto.MemberNickNameDto> getUserNicknames(@RequestParam List<Long> ids){

		List<MemberDto.MemberNickNameDto> nicknameList = memberService.getUserNicknames(ids);

		return nicknameList;
	}
}