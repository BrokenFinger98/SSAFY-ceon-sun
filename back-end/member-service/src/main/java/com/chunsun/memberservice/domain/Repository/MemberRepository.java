package com.chunsun.memberservice.domain.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chunsun.memberservice.domain.Entity.Member;
import com.chunsun.memberservice.domain.Enum.Gender;
import com.chunsun.memberservice.domain.Enum.Role;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

	boolean existsByNickname(String nickname);

	boolean existsById(Long id);

	Page<Member> findAll(Specification<Member> spec, Pageable pageable);

	@Query("SELECT m.role FROM Member m WHERE m.id = :id")
	Role findRoleById(@Param("id") Long id);

	@Query("SELECT m FROM Member m " +
		"JOIN Student s ON m.id = s.id " +
		"WHERE m.role = :role " +
		"AND (:gender IS NULL OR m.gender = :gender) " +
		"AND (:minBirthdate IS NULL OR m.birthdate >= :minBirthdate) " +
		"AND (:maxBirthdate IS NULL OR m.birthdate <= :maxBirthdate) " +
		"ORDER BY s.updatedAt DESC")
	Page<Member> findFilteredStudentMembers(
		@Param("role") Role role,
		@Param("gender") Gender genderParam, // Gender를 String 또는 enum으로 처리할 수 있음
		@Param("minBirthdate") LocalDate minBirthdate,
		@Param("maxBirthdate") LocalDate maxBirthdate,
		Pageable pageable);

	@Query("SELECT m FROM Member m " +
		"JOIN Teacher t ON m.id = t.id " +
		"WHERE m.role = :role " +
		"AND (:gender IS NULL OR m.gender = :gender) " +
		"AND (:minBirthdate IS NULL OR m.birthdate >= :minBirthdate) " +
		"AND (:maxBirthdate IS NULL OR m.birthdate <= :maxBirthdate) " +
		"ORDER BY t.updatedAt DESC")
	Page<Member> findFilteredTeacherMembers(
		@Param("role") Role role,
		@Param("gender") Gender genderParam,
		@Param("minBirthdate") LocalDate minBirthdate,
		@Param("maxBirthdate") LocalDate maxBirthdate,
		Pageable pageable);

	@Query("select distinct m from Member m " +
		"left join fetch m.memberCategories mc " +
		"left join fetch mc.category " +
		"where m.id in :ids")
	List<Member> findAllWithCategoriesById(@Param("ids") List<Long> ids);

	@Query("SELECT DISTINCT m FROM Member m " +
		"LEFT JOIN FETCH m.memberCategories mc " +
		"LEFT JOIN FETCH mc.category " +
		"WHERE m.id = :id")
	Optional<Member> findWithCategoriesById(@Param("id") Long id);


}
