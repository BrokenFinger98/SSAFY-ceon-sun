package com.chunsun.memberservice.application.service;

import static com.chunsun.memberservice.application.dto.MemberDto.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.chunsun.memberservice.application.dto.MemberDto;

public interface MemberService {

	SignUpResponse signUp(final SignUpRequest request);

	void updateMemberInfo(final UpdateInfoRequest request);

	GetInfoResponse getMemberInfo(final Long id);

	void deleteMember(final Long id);

	void checkNicknameAvailability(String nickname);

	Boolean isDeleted(Long memberId);

	List<TeacherListItem> getTeachersRank(List<TeacherTupleDto> teachersRank);

	List<MemberNickNameDto> getUserNicknames(List<Long> ids);

	List<MemberPaymentDto> getMembersInfo(List<Long> memberIds);

	String getRole(Long id);

	Page<SearchMemberResponse> searchMembers(
		final Long memberId,
		final List<Long> categories,
		final String gender,
		final Integer startAge,
		final Integer endAge,
		final Pageable pageable);
}
