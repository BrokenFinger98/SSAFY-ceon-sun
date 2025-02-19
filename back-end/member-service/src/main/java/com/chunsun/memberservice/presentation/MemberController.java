package com.chunsun.memberservice.presentation;

import static com.chunsun.memberservice.application.dto.MemberDto.*;
import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chunsun.memberservice.application.service.MemberService;
import com.chunsun.memberservice.common.resolver.UserId;
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
	public ResponseEntity<SignUpResponse> signUpMember(@Valid @RequestBody final SignUpRequest request) {
		return ResponseEntity.status(CREATED).body(memberService.signUp(request));
	}

	@UserId
	@PutMapping("/{id}")
	public ResponseEntity<Void> updateMemberProfile(
		@UserId final Long memberId,
		@PathVariable final Long id,
		@RequestPart(value = "nickname", required = false) String nickname,
		@RequestPart(value = "profileImage", required = false) MultipartFile image) {
		HeaderUtil.validateUserId(id, memberId);

		final UpdateInfoRequest request = new UpdateInfoRequest(id, nickname, image);
		memberService.updateMemberInfo(request);

		return ResponseEntity.status(NO_CONTENT).build();
	}

	@UserId
	@GetMapping("/{id}")
	public ResponseEntity<GetInfoResponse> getMemberProfile(@UserId final Long memberId, @PathVariable final Long id) {
		HeaderUtil.validateUserId(id, memberId);
		return ResponseEntity.ok(memberService.getMemberInfo(id));
	}

	@UserId
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMember(@UserId final Long memberId, @PathVariable final Long id) {
		HeaderUtil.validateUserId(id, memberId);
		memberService.deleteMember(id);
		return ResponseEntity.status(NO_CONTENT).build();
	}

	@GetMapping()
	public ResponseEntity<Void> validateNickname(
		@RequestParam final String nickname) {
		memberService.checkNicknameAvailability(nickname);
		return ResponseEntity.ok().build();
	}

	@UserId
	@GetMapping("/{id}/search")
	public ResponseEntity<Page<SearchMemberResponse>> searchMembers(
		@UserId final Long memberId,
		@PathVariable final Long id,
		@RequestParam(required = false) List<Long> categories,
		@RequestParam(required = false) String gender,
		@RequestParam(required = false) Integer startAge,
		@RequestParam(required = false) Integer endAge,
		@PageableDefault(size = 10) final Pageable pageable) {
		HeaderUtil.validateUserId(id, memberId);
		return ResponseEntity.ok(memberService.searchMembers(memberId, categories, gender, startAge, endAge, pageable));
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