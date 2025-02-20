package com.chunsun.memberservice.application.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.memberservice.application.dto.StudentDto;
import com.chunsun.memberservice.common.error.GlobalErrorCodes;
import com.chunsun.memberservice.common.exception.BusinessException;
import com.chunsun.memberservice.domain.Entity.Category;
import com.chunsun.memberservice.domain.Entity.Member;
import com.chunsun.memberservice.domain.Entity.MemberCategory;
import com.chunsun.memberservice.domain.Repository.MemberRepository;
import com.chunsun.memberservice.domain.Enum.Role;
import com.chunsun.memberservice.domain.Entity.Student;
import com.chunsun.memberservice.domain.Repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

	private final StudentRepository studentRepository;
	private final MemberRepository memberRepository;
	private final CategoryService categoryService;

	@Override
	@Transactional
	public StudentDto.CardResponse createCard(Long id, StudentDto.CreateCardRequest request) {

		Member member = memberRepository.findById(id).orElseThrow(()
			-> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

		Role role = member.getRole();

		if(role == Role.TEACHER) {
			throw new BusinessException(GlobalErrorCodes.ALREADY_TEACHER);
		}
		if(role == Role.STUDENT) {
			throw new BusinessException(GlobalErrorCodes.ALREADY_STUDENT);
		}

		member.updateRole(Role.STUDENT);

		Student student = new Student(
			member,
			request.isExposed(),
			request.description()
		);

		studentRepository.save(student);

		return new StudentDto.CardResponse("학생 카드 생성 완료");
	}

	@Override
	@Transactional
	public StudentDto.CardResponse updateCard(Long id, StudentDto.UpdateCardRequest request) {

		Student student = studentRepository.findById(id).orElseThrow(()
			-> new BusinessException(GlobalErrorCodes.STUDENT_NOT_FOUND));

		Member member = memberRepository.findById(id).orElseThrow(
			() -> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

		if(member.getRole() != Role.STUDENT) {
			throw new BusinessException(GlobalErrorCodes.NOT_STUDENT);
		}

		student.updateCard(
			request.isExposed(),
			request.description()
		);

		return new StudentDto.CardResponse("학생 카드 업데이트 완료");
	}

	@Override
	public StudentDto.GetCardResponse getCard(Long id) {

		Student student = studentRepository.findById(id).orElseThrow(()
			-> new BusinessException(GlobalErrorCodes.STUDENT_NOT_FOUND));

		return new StudentDto.GetCardResponse(
			student.getIsExposed(),
			student.getDescription(),
			categoryService.getUserCategories(id)
		);
	}

	@Override
	public StudentDto.GetDetailResponse getDetail(Long id) {

		Member memberInfo = memberRepository.findWithCategoriesById(id).orElseThrow(()
			-> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

		Student studentInfo = studentRepository.findById(id).orElseThrow(()
			-> new BusinessException(GlobalErrorCodes.STUDENT_NOT_FOUND));

		List<Category> categories = memberInfo.getMemberCategories().stream()
			.map(MemberCategory::getCategory)
			.collect(Collectors.toList());

		return new StudentDto.GetDetailResponse(
			memberInfo.getName(),
			memberInfo.getProfileImage(),
			memberInfo.getNickname(),
			memberInfo.getGender(),
			Period.between(memberInfo.getBirthdate(), LocalDate.now()).getYears(),
			studentInfo.getDescription(),
			categories
		);
	}

	@Override
	public StudentDto.GetListResponse getList() {

		return new StudentDto.GetListResponse(
			studentRepository.findAllStudentIds()
		);
	}
}
