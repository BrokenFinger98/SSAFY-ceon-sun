package com.chunsun.memberservice.application.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.chunsun.memberservice.domain.Repository.StudentRepository;
import com.chunsun.memberservice.domain.Repository.TeacherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final CategoryRepository categoryRepository;
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final MemberCategoryRepository memberCategoryRepository;
	private final S3Service s3Service;

	@Override
	@Transactional
	public MemberDto.SignUpResponse signUp(MemberDto.SignUpRequest request) {

		Member member = Member.builder()
			.kakaoId(request.kakaoId())
			.email(request.email())
			.name(request.name())
			.nickname(request.nickname())
			.birthdate(request.birthdate())
			.gender(request.gender())
			.role(Role.GUEST)
			.build();
		memberRepository.save(member);

		return new MemberDto.SignUpResponse("가입 완료");
	}

	@Override
	@Transactional
	public void deleteMember(Long id) {

		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

		memberCategoryRepository.deleteByMemberId(id);

		if (member.getRole() == Role.STUDENT) {
			studentRepository.deleteById(member.getId());
		} else if (member.getRole() == Role.TEACHER) {
			teacherRepository.deleteById(member.getId());
		}
		member.markDeleted();
	}

	@Override
	public void checkNicknameAvailability(String nickname) {

		if(memberRepository.existsByNickname(nickname)) {
			throw new BusinessException(GlobalErrorCodes.DUPLICATE_NICKNAME);
		}
	}

	@Override
	@Transactional
	public MemberDto.UpdateInfoResponse updateMemberInfo(MemberDto.UpdateInfoRequest request) {

		Member member = memberRepository.findById(request.id())
			.orElseThrow(() -> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

		String profile = member.getProfileImage();

		if (request.profileImage() != null && !request.profileImage().isEmpty()) {
			try {
				if(profile!=null && !profile.isEmpty()) {
					s3Service.deleteImage(profile);
				}
				profile = s3Service.uploadImage(request.profileImage());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			if (profile != null && !profile.isEmpty()) {
				s3Service.deleteImage(profile);
			}
			profile = "";
		}
		member.updateInfo(request.nickname(), profile);

		return new MemberDto.UpdateInfoResponse();
	}

	@Override
	public MemberDto.GetInfoResponse getMemberInfo(Long id) {

		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

		return new MemberDto.GetInfoResponse(
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

		return !memberRepository.existsById(id);
	}

	@Override
	public Page<MemberDto.MemberListItem> getFilterMembers(
		String category, String gender, String age, int page, int size, Long userId) {

		// 1. 요청자(Member) 조회
		Member requestingUser = memberRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

		Role requesterRole = requestingUser.getRole();
		Role searchTargetRole;
		if (requesterRole == Role.TEACHER) {
			searchTargetRole = Role.STUDENT;
		} else if (requesterRole == Role.STUDENT) {
			searchTargetRole = Role.TEACHER;
		} else {
			throw new BusinessException(GlobalErrorCodes.GUEST_NOT_ALLOWED);
		}

		// 2. 필터링 조건 준비 (성별, 연령대 범위 계산 등)
		Gender genderParam = (gender != null && !gender.isEmpty())
			? Gender.valueOf(gender.toUpperCase())
			: null;

		LocalDate minBirthdate = null;
		LocalDate maxBirthdate = null;
		if (age != null && age.contains("-")) {
			String[] ageParts = age.split("-");
			try {
				int lowerAge = Integer.parseInt(ageParts[0]);
				int upperAge = Integer.parseInt(ageParts[1]);

				if (lowerAge > upperAge) {
					throw new BusinessException(GlobalErrorCodes.INVALID_AGE_RANGE);
				}
				LocalDate today = LocalDate.now();
				// 나이가 lowerAge ~ upperAge 인 회원의 생년월일 범위 계산
				minBirthdate = today.minusYears(upperAge);
				maxBirthdate = today.minusYears(lowerAge);
			} catch (NumberFormatException e) {
				throw new BusinessException(GlobalErrorCodes.INVALID_AGE_FORMAT);
			}
		}

		// 3. 카테고리 필터 처리 (JPQL에 포함하지 않고 최종 DTO 매핑 시 별도 사용)
		List<Category> filterCategories;
		if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("none")) {
			List<String> categoryNameList = List.of(category.split(","));

			List<Category> categories = categoryRepository.findByNameIgnoreCaseIn(categoryNameList);
			if (categories.size() != categoryNameList.size()) {
				throw new BusinessException(GlobalErrorCodes.CATEGORY_NOT_FOUND);
			}

			filterCategories = categories;
		} else {
			filterCategories = null;
		}

		// 4. JPQL로 대상 회원 조회
		Pageable pageable = PageRequest.of(page, size);
		Page<Member> resultPage;
		if (searchTargetRole == Role.STUDENT) {
			resultPage = memberRepository.findFilteredStudentMembers(
				searchTargetRole, genderParam, minBirthdate, maxBirthdate, pageable);
		} else {
			resultPage = memberRepository.findFilteredTeacherMembers(
				searchTargetRole, genderParam, minBirthdate, maxBirthdate, pageable);
		}

		// 5. 조회된 회원 리스트 DTO 매핑
		List<Member> members = resultPage.getContent();

		// IN절을 이용하여, 조회된 모든 회원의 MemberCategory(및 Category)를 한 번의 쿼리로 가져옴
		List<MemberCategory> allMemberCategories = memberCategoryRepository.findAllByMembers(members);

		// 회원 ID별로 Category 리스트 그룹핑
		Map<Long, List<Category>> categoriesByMemberId = allMemberCategories.stream()
			.collect(Collectors.groupingBy(
				mc -> mc.getMember().getId(),
				Collectors.mapping(MemberCategory::getCategory, Collectors.toList())
			));

		// 각 회원에 대해 DTO 매핑 (카테고리 필터가 있다면 추가 필터링)
		List<MemberDto.MemberListItem> dtoList = members.stream().map(member -> {
			Long memberId = member.getId();
			String profileImage = member.getProfileImage();
			String nickname = member.getNickname();
			int memberAge = Period.between(member.getBirthdate(), LocalDate.now()).getYears();
			Gender memberGender = member.getGender();
			// 그룹화된 결과에서 해당 회원의 Category 리스트 조회 (없으면 빈 리스트)
			List<Category> memberCategories = categoriesByMemberId.getOrDefault(memberId, Collections.emptyList());

			// 카테고리 필터 적용: 설정된 필터 카테고리를 모두 포함하지 않으면 해당 회원은 제외
			if (filterCategories != null && !memberCategories.containsAll(filterCategories)) {
				return null;
			}
			return new MemberDto.MemberListItem(memberId, profileImage, nickname, memberAge, memberGender, memberCategories);
		}).filter(Objects::nonNull).collect(Collectors.toList());

		return new PageImpl<>(dtoList, pageable, resultPage.getTotalElements());
	}

	@Override
	public List<MemberDto.TeacherListItem> getTeachersRank(List<MemberDto.TeacherTupleDto> teachersRank) {

		List<Long> memberIds = teachersRank.stream()
			.map(dto -> Long.parseLong(dto.value()))
			.toList();

		List<Member> members = memberRepository.findAllWithCategoriesById(memberIds);

		Map<Long, Member> teacherMap = members.stream()
			.collect(Collectors.toMap(Member::getId, t -> t));

		return IntStream.range(0, memberIds.size())
			.mapToObj(i -> {
				Long id = memberIds.get(i);
				Member member = teacherMap.get(id);
				Integer age = Period.between(member.getBirthdate(), LocalDate.now()).getYears();

				List<Category> categories = member.getMemberCategories().stream()
					.map(MemberCategory::getCategory)
					.collect(Collectors.toList());

				return new MemberDto.TeacherListItem(
					member.getId(),
					member.getProfileImage(),
					member.getNickname(),
					age,
					member.getGender(),
					categories,
					teachersRank.get(i).score()
				);
			})
			.collect(Collectors.toList());
	}

	@Override
	public List<MemberDto.MemberNickNameDto> getUserNicknames(List<Long> memberIds) {

		List<Member> memberList = memberRepository.findAllById(memberIds);

		return memberList.stream().map(member -> {
			return new MemberDto.MemberNickNameDto(
				member.getId(),
				member.getNickname(),
				member.getProfileImage()
			);
		}).collect(Collectors.toList());
	}

	@Override
	public List<MemberDto.MemberPaymentDto> getMembersInfo(List<Long> memberIds) {

		List<Member> memberList = memberRepository.findAllWithCategoriesById(memberIds);

		return memberList.stream().map(member -> {
			Integer age = Period.between(member.getBirthdate(), LocalDate.now()).getYears();

			List<String> categoryNames = member.getMemberCategories().stream()
				.map(mc -> mc.getCategory().getName())
				.collect(Collectors.toList());

			return new MemberDto.MemberPaymentDto(
				member.getId(),
				member.getNickname(),
				member.getGender().toString(),
				age,
				member.getProfileImage(),
				categoryNames
			);
		}).collect(Collectors.toList());
	}

	@Override
	public String getRole(Long id) {

		return memberRepository.findRoleById(id).toString();
	}
}