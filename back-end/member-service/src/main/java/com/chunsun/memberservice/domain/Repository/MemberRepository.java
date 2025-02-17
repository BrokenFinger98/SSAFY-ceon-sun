package com.chunsun.memberservice.domain.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.chunsun.memberservice.domain.Entity.Member;

import feign.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

	boolean existsByKakaoId(String kakaoId);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	boolean existsById(Long id);

	Page<Member> findAll(Specification<Member> spec, Pageable pageable);

}
