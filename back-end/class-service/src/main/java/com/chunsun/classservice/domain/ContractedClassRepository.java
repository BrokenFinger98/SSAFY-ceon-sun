package com.chunsun.classservice.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractedClassRepository extends JpaRepository<ContractedClass, Long> {
	Optional<ContractedClass> findById(Long id);
	Page<ContractedClass> findAllByTeacherId(Long teacherId, Pageable pageable);
	Page<ContractedClass> findAllByStudentId(Long studentId, Pageable pageable);
}
