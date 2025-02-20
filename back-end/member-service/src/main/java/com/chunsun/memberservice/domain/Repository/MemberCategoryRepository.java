package com.chunsun.memberservice.domain.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chunsun.memberservice.domain.Entity.Member;
import com.chunsun.memberservice.domain.Entity.MemberCategory;

import jakarta.transaction.Transactional;

public interface MemberCategoryRepository extends JpaRepository<MemberCategory, Long> {


	List<MemberCategory> findByMember(Member member);

	@Modifying
	@Query(value = "INSERT INTO member_category (member_id, category_id) " +
		"SELECT :memberId, c.id FROM category c " +
		"WHERE c.id IN (:categoryIds)", nativeQuery = true)
	void insertMemberCategory(@Param("memberId") Long memberId, @Param("categoryIds") List<Long> categoryIds);

	@Modifying
	@Query("DELETE FROM MemberCategory mc WHERE mc.member.id = :memberId")
	void deleteByMemberId(@Param("memberId") Long memberId);

	@Query("select mc from MemberCategory mc join fetch mc.category where mc.member in :members")
	List<MemberCategory> findAllByMembers(@Param("members") List<Member> members);

}
