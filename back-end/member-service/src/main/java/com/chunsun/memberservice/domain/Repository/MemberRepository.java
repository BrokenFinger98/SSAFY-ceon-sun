package com.chunsun.memberservice.domain.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chunsun.memberservice.domain.Entity.Member;
import com.chunsun.memberservice.domain.Enum.Role;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

	boolean existsByKakaoId(String kakaoId);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	boolean existsById(Long id);

	Page<Member> findAll(Specification<Member> spec, Pageable pageable);

	boolean existsByKakaoIdOrEmail(String kakaoId, String email);

	@Query("SELECT m.role FROM Member m WHERE m.id = :id")
	Role findRoleById(@Param("id") Long id);
}
