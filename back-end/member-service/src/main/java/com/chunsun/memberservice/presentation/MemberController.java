package com.chunsun.memberservice.presentation;

import static com.chunsun.memberservice.application.dto.MemberDto.*;
import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chunsun.memberservice.application.dto.MemberDto;
import com.chunsun.memberservice.application.service.MemberService;
import com.chunsun.memberservice.common.util.HeaderUtil;
import com.chunsun.memberservice.config.feign.RankClient;
import com.chunsun.memberservice.domain.Repository.MemberRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

	private final MemberService memberService;
	private final MemberRepository memberRepository;
	private final RankClient rankClient;

	@PostMapping
	public ResponseEntity<SignUpResponse> signUpMember(
		@Valid @RequestBody final SignUpRequest request) {
		return ResponseEntity.status(CREATED).body(memberService.signUp(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UpdateInfoResponse> updateMemberProfile(
		@RequestHeader("X-User-ID") Long memberId,
		@PathVariable Long id,
		@RequestPart(value = "nickname", required = false) String nickname,
		@RequestPart(value = "profileImage", required = false) MultipartFile image) {
		HeaderUtil.validateUserId(id, memberId);

		UpdateInfoRequest request = new UpdateInfoRequest(id, nickname, image);

		UpdateInfoResponse updateInfo = memberService.updateMemberInfo(request);

		return ResponseEntity.ok(updateInfo);
	}

	@GetMapping("/{id}")
	public ResponseEntity<GetInfoResponse> getMemberProfile(
		@RequestHeader("X-User-ID") Long memberId,
		@PathVariable Long id) {
		HeaderUtil.validateUserId(id, memberId);

		GetInfoResponse getInfo = memberService.getMemberInfo(id);

		return ResponseEntity.ok(getInfo);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMember(
		@RequestHeader("X-User-ID") Long memberId,
		@PathVariable Long id) {
		HeaderUtil.validateUserId(id, memberId);

		memberService.deleteMember(id);

		return ResponseEntity.ok().build();
	}

	@GetMapping()
	public ResponseEntity<Void> validateNickname(
		@RequestParam String nickname) {

		memberService.checkNicknameAvailability(nickname);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}/search")
	public ResponseEntity<Page<MemberListItem>> searchMembers(
		@RequestHeader("X-User-ID") Long memberId,
		@PathVariable Long id,
		@RequestParam(required = false) String category,
		@RequestParam(required = false) String gender,
		@RequestParam(required = false) String age,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		HeaderUtil.validateUserId(id, memberId);

		Page<MemberListItem> result = memberService.getFilterMembers(category, gender, age, page, size, id);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/exist/{id}")
	public boolean checkMemberExists(
		@PathVariable Long id) {

		return memberRepository.existsById(id);
	}

	@GetMapping("/delete/{id}")
	public boolean checkMemberDeleted(
		@PathVariable Long id) {

		return memberService.isDeleted(id);
	}

	@GetMapping("/ranking")
	public ResponseEntity<List<TeacherListItem>> getTeacherRanking(){

		List<TeacherTupleDto> teachersRank = rankClient.getTeachersRank();

		List<TeacherListItem> result = memberService.getTeachersRank(teachersRank);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/nicknames")
	public List<MemberNickNameDto> getUserNicknames(@RequestParam List<Long> ids){

		List<MemberNickNameDto> nicknameList = memberService.getUserNicknames(ids);

		return nicknameList;
	}

	@GetMapping("/payments")
	public List<MemberPaymentDto> getMemberList(@RequestParam List<Long> memberIds){

		return memberService.getMembersInfo(memberIds);
	}

	@GetMapping("/role")
	public String getRole(@RequestParam Long memberId){

		return memberService.getRole(memberId).toLowerCase();
	}
}