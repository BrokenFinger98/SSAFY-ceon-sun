package com.chunsun.memberservice.application.service;

import static com.chunsun.memberservice.application.dto.MemberDto.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.memberservice.application.dto.MemberDto;
import com.chunsun.memberservice.common.error.GlobalErrorCodes;
import com.chunsun.memberservice.common.exception.BusinessException;
import com.chunsun.memberservice.domain.Entity.Category;
import com.chunsun.memberservice.domain.Repository.CategoryRepository;
import com.chunsun.memberservice.domain.Enum.Gender;
import com.chunsun.memberservice.domain.Entity.Member;
import com.chunsun.memberservice.domain.Entity.MemberCategory;
import com.chunsun.memberservice.domain.Repository.MemberCategoryRepository;
import com.chunsun.memberservice.domain.Repository.MemberRepository;
import com.chunsun.memberservice.domain.Enum.Role;
import com.chunsun.memberservice.domain.Entity.Student;
import com.chunsun.memberservice.domain.Repository.StudentRepository;
import com.chunsun.memberservice.domain.Entity.Teacher;
import com.chunsun.memberservice.domain.Repository.TeacherRepository;
import com.chunsun.memberservice.infrastructure.QueryDslRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

	private final QueryDslRepository queryDslRepository;
	private final MemberRepository memberRepository;
	private final CategoryRepository categoryRepository;
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final MemberCategoryRepository memberCategoryRepository;
	private final S3Service s3Service;

	@Override
	@Transactional
	public SignUpResponse signUp(SignUpRequest request) {
		memberRepository.save(Member.builder()
			.kakaoId(request.kakaoId())
			.email(request.email())
			.name(request.name())
			.nickname(request.nickname())
			.birthdate(request.birthdate())
			.gender(request.gender())
			.role(Role.GUEST)
			.build());

		return new SignUpResponse("가입 완료");
	}

	@Override
	@Transactional
	public void deleteMember(final Long memberId) {
		memberRepository.deleteById(memberId);
		memberCategoryRepository.deleteByMemberId(memberId);
	}

	@Override
	public void checkNicknameAvailability(String nickname) {
		if (memberRepository.existsByNickname(nickname)) {
			throw new BusinessException(GlobalErrorCodes.DUPLICATE_NICKNAME);
		}
	}

	@Override
	@Transactional
	public void updateMemberInfo(final UpdateInfoRequest request) {
		final Member member = memberRepository.findById(request.id())
			.orElseThrow(() -> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

		String profile = member.getProfileImage();

		if (request.profileImage() != null && !request.profileImage().isEmpty()) {
			try {
				if (profile != null && !profile.isEmpty()) {
					s3Service.deleteImage(profile);
				}
				profile = s3Service.uploadImage(request.profileImage());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			s3Service.deleteImage(profile);
			profile = "";
		}

		member.updateInfo(request.nickname(), profile);
	}

	@Override
	public GetInfoResponse getMemberInfo(final Long id) {
		final Member member = memberRepository.findById(id)
			.orElseThrow(() -> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

		return new GetInfoResponse(
			member.getName(),
			member.getNickname(),
			member.getEmail(),
			member.getBirthdate(),
			member.getGender(),
			member.getProfileImage()
		);
	}

	@Override
	public Boolean isDeleted(Long id) {

		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

		return member.getDeletedAt() == null;
	}

	@Override
	public List<TeacherListItem> getTeachersRank(List<TeacherTupleDto> teachersRank) {

		List<Long> ids = teachersRank.stream()
			.map(dto -> Long.parseLong(dto.value()))
			.toList();

		List<Member> teachers = memberRepository.findAllById(ids);

		Map<Long, Member> teacherMap = teachers.stream()
			.collect(Collectors.toMap(Member::getId, t -> t));

		List<TeacherListItem> rankedTeachers = new ArrayList<>();

		for (TeacherTupleDto rankInfo : teachersRank) {
			Long teacherId = Long.parseLong(rankInfo.value());
			Member teacher = teacherMap.get(teacherId);

			if (teacher != null) {
				List<Category> memberCategories = memberCategoryRepository.findByMember(teacher).stream()
					.map(MemberCategory::getCategory)
					.collect(Collectors.toList());

				Integer age = Period.between(teacher.getBirthdate(), LocalDate.now()).getYears();

				TeacherListItem item = new TeacherListItem(
					teacher.getId(),
					teacher.getProfileImage(),
					teacher.getNickname(),
					age,
					teacher.getGender(),
					memberCategories,
					rankInfo.score()
				);
				rankedTeachers.add(item);
			}
		}
		return rankedTeachers;
	}

	@Override
	public List<MemberNickNameDto> getUserNicknames(List<Long> ids) {

		List<MemberNickNameDto> nicknames = new ArrayList<>();
		for (Long id : ids) {
			Member member = memberRepository.findById(id).
				orElseThrow(() -> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

			MemberNickNameDto nickname = new MemberNickNameDto(
				member.getId(),
				member.getNickname(),
				member.getProfileImage()
			);
			nicknames.add(nickname);
		}
		return nicknames;
	}

	@Override
	public List<MemberPaymentDto> getMembersInfo(List<Long> memberIds) {

	}

	@Override
	public String getRole(Long id) {
		return memberRepository.findRoleById(id).toString();
	}

	@Override
	public Page<SearchMemberResponse> searchMembers(final Long memberId, final List<Long> categories,
		final String genderString, final Integer startAge, final Integer endAge, final Pageable pageable) {

		final Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
			Sort.by("createdAt").descending());

		final Gender gender;
		if(genderString.equalsIgnoreCase(Gender.MALE.toString())) {
			gender = Gender.MALE;
		}else if(genderString.equalsIgnoreCase(Gender.FEMALE.toString())) {
			gender = Gender.FEMALE;
		}else throw new BusinessException(GlobalErrorCodes.INVALID_GENDER);

		return queryDslRepository.searchMembers(memberId, categories, gender, startAge, endAge, sortedPageable);
	}
}