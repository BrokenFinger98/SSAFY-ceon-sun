package com.chunsun.memberservice.domain.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chunsun.memberservice.domain.Entity.Member;
import com.chunsun.memberservice.domain.Entity.MemberCategory;

import jakarta.transaction.Transactional;

public interface MemberCategoryRepository extends JpaRepository<MemberCategory, Long> {

	@Transactional
	void deleteByMemberId(Long memberId);

	List<MemberCategory> findByMember(Member member);

	List<MemberCategory> findByMemberId(Long memberId);
}
