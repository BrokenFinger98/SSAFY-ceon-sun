package com.chunsun.classservice.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRequestRepository extends JpaRepository<ClassRequest, Long> {
	Page<ClassRequest> findAllByTeacherId(Long teacherId, Pageable pageable);
	Page<ClassRequest> findAllByStudentId(Long studentId, Pageable pageable);
	Optional<Integer> deleteByIdAndTeacherIdAndStudentId(Long classRequestId, Long teacherId, Long studentId);
	Optional<ClassRequest> findByTeacherIdAndStudentId(Long teacherId, Long studentId);
}
