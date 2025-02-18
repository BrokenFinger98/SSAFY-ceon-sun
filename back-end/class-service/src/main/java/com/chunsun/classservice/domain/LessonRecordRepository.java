package com.chunsun.classservice.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRecordRepository extends JpaRepository<LessonRecord, Long> {
	Page<LessonRecord> findAllByContractedClass(final ContractedClass contractedClass, final Pageable pageable);
	Optional<LessonRecord> findByContractedClassAndStudentJoinTimeIsNull(ContractedClass contractedClass);
	Optional<LessonRecord> findByContractedClassAndTeacherJoinTimeIsNull(ContractedClass contractedClass);
	Optional<LessonRecord> findByContractedClassAndStudentExitTimeIsNull(ContractedClass contractedClass);
	Optional<LessonRecord> findByContractedClassAndTeacherExitTimeIsNull(ContractedClass contractedClass);
}
