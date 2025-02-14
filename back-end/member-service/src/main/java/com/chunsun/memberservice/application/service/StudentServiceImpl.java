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
import com.chunsun.memberservice.domain.Repository.CategoryRepository;
import com.chunsun.memberservice.domain.Entity.Member;
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

	// 카드 생성
	@Override
	@Transactional
	public StudentDto.CardResponse createCard(StudentDto.CreateCardRequest request) {

		Member member = memberRepository.findById(request.id()).orElseThrow(()
			-> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

		if(member.getRole() == Role.TEACHER) {
			throw new BusinessException(GlobalErrorCodes.ALREADY_TEACHER);
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

	// 카드 업데이트
	@Override
	@Transactional
	public StudentDto.CardResponse updateCard(Long id, StudentDto.UpdateCardRequest request) {
		Student student = studentRepository.findById(id).orElseThrow(()
			-> new BusinessException(GlobalErrorCodes.STUDENT_NOT_FOUND));

		if(student.getMember().getRole() != Role.STUDENT) {
			throw new BusinessException(GlobalErrorCodes.NOT_STUDENT);
		}

		student.updateCard(
			request.isExposed(),
			request.description()
		);

		studentRepository.save(student);

		return new StudentDto.CardResponse("학생 카드 업데이트 완료");
	}

	// 카드 조회
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

	// 카드 상세조회(남이 보는거)
	@Override
	public StudentDto.GetDetailResponse getDetail(Long id) {

		Member memberInfo = memberRepository.findById(id).orElseThrow(()
			-> new BusinessException(GlobalErrorCodes.USER_NOT_FOUND));

		Student studentInfo = studentRepository.findById(id).orElseThrow(()
			-> new BusinessException(GlobalErrorCodes.STUDENT_NOT_FOUND));

		return new StudentDto.GetDetailResponse(
			memberInfo.getName(),
			memberInfo.getProfileImage(),
			memberInfo.getNickname(),
			memberInfo.getGender(),
			Period.between(memberInfo.getBirthdate(), LocalDate.now()).getYears(),
			studentInfo.getDescription(),
			categoryService.getUserCategories(id)
		);
	}

	// 학생 아이디
	@Override
	public StudentDto.GetListResponse getList() {

		List<Long> studentIds = studentRepository.findAll().stream().map(Student::getId).collect(Collectors.toList());

		return new StudentDto.GetListResponse(
			studentIds
		);
	}
}
