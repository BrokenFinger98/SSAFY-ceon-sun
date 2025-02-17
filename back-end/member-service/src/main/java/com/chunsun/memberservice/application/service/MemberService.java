package com.chunsun.memberservice.application.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.chunsun.memberservice.application.dto.MemberDto;

public interface MemberService {

	MemberDto.SignUpResponse signUp(MemberDto.SignUpRequest request);

	MemberDto.UpdateInfoResponse updateMemberInfo(MemberDto.UpdateInfoRequest request);

	MemberDto.GetInfoResponse getMemberInfo(Long id);

	void deleteMember(Long id);

	void checkNicknameAvailability(String nickname);

	Boolean isDeleted(Long memberId);

	Page<MemberDto.MemberListItem> getFilterMembers(String category, String gender, String age, int page, int size, Long userId);

	List<MemberDto.TeacherListItem> getTeachersRank(List<MemberDto.TeacherTupleDto> teachersRank);

	List<MemberDto.MemberNickNameDto> getUserNicknames(List<Long> ids);

	List<MemberDto.MemberListItem> getMembersInfo(List<Long> ids);
}
