package com.chunsun.memberservice.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import com.chunsun.memberservice.domain.Entity.Category;
import com.chunsun.memberservice.domain.Entity.Member;
import com.chunsun.memberservice.domain.Entity.MemberCategory;
import com.chunsun.memberservice.domain.Entity.Student;
import com.chunsun.memberservice.domain.Enum.Gender;
import com.chunsun.memberservice.domain.Enum.Role;

public class MemberSpecification {

	// 역할
	public static Specification<Member> memberIdEquals(Role role) {
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.equal(root.get("role"), role);
	}

	// 학생일 경우 isExposed에 따라 노출 여부 결정됨
	public static Specification<Member> isExposedTrue() {
		return (root, query, criteriaBuilder) -> {
			Join<Member, Student> studentJoin = root.join("student");
			return criteriaBuilder.equal(studentJoin.get("isExposed"), true);
		};
	}


	// 성별 필터
	public static Specification<Member> hasGender(Gender gender) {
		return ((root, query, criteriaBuilder) ->
			criteriaBuilder.equal(root.get("gender"), gender));
	}

	// 연령대 필터
	public static Specification<Member> hasAgeBetween(Integer minAge, Integer maxAge) {
		LocalDate now = LocalDate.now();
		LocalDate maxBirthdate = now.minusYears(minAge);
		// 예: 29세 만족 → 생년월일은 지금부터 30년 전보다 이전이어야 함
		LocalDate minBirthdate = now.minusYears(maxAge + 1).plusDays(1);
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.between(root.get("birthdate"), minBirthdate, maxBirthdate);
	}

	// 카테고리 필터: Member의 memberCategories를 JOIN하여 Category id 비교
	public static Specification<Member> hasAllCategories(List<Category> categories) {
		return (root, query, criteriaBuilder) -> {
			// 만약 필터링할 카테고리가 없으면 조건 없이 true(conjunction) 반환
			if (categories == null || categories.isEmpty()) {
				return criteriaBuilder.conjunction();
			}

			// 서브쿼리 생성: 해당 Member가 가진 카테고리 중 요청한 카테고리의 개수를 카운트
			Subquery<Long> subquery = query.subquery(Long.class);
			var mcRoot = subquery.from(MemberCategory.class);
			subquery.select(criteriaBuilder.countDistinct(mcRoot.get("category").get("id")));
			subquery.where(
				criteriaBuilder.equal(mcRoot.get("member"), root),
				mcRoot.get("category").get("id").in(
					categories.stream().map(Category::getId).collect(Collectors.toList())
				)
			);

			// 서브쿼리 결과(카운트)가 요청한 카테고리 수와 같으면 해당 Member는 모두 보유한 것
			return criteriaBuilder.equal(subquery, (long) categories.size());
		};
	}
}
