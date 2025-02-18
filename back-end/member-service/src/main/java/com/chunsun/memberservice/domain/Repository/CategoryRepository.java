package com.chunsun.memberservice.domain.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chunsun.memberservice.domain.Entity.Category;

import feign.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Optional<Category> findByNameIgnoreCase(String name);

	@Query("select c from MemberCategory mc join mc.category c where mc.member.id = :memberId")
	List<Category> findCategoriesByMemberId(@Param("memberId") Long memberId);
}
