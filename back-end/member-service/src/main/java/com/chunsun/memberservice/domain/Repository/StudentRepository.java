package com.chunsun.memberservice.domain.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chunsun.memberservice.domain.Entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

	@Query("select s.id from Student s")
	List<Long> findAllStudentIds();
}
