package com.chunsun.memberservice.infrastructure;

import static com.chunsun.memberservice.application.dto.CategoryDto.*;
import static com.chunsun.memberservice.application.dto.MemberDto.*;
import static com.chunsun.memberservice.domain.Entity.QCategory.*;
import static com.chunsun.memberservice.domain.Entity.QMember.*;
import static com.chunsun.memberservice.domain.Entity.QMemberCategory.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.chunsun.memberservice.application.dto.CategoryDto;
import com.chunsun.memberservice.domain.Entity.Member;
import com.chunsun.memberservice.domain.Entity.QCategory;
import com.chunsun.memberservice.domain.Entity.QMember;
import com.chunsun.memberservice.domain.Entity.QMemberCategory;
import com.chunsun.memberservice.domain.Enum.Gender;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class QueryDslRepository {

	private final JPAQueryFactory queryFactory;

	// TODO 개발 해야함
	public Page<SearchMemberResponse> searchMembers(final Long memberId, final List<Long> categoryIds,
		final Gender gender, final Integer startAge,
		final Integer endAge, final Pageable pageable) {

		final LocalDate now = LocalDate.now();
		final LocalDate maxBirthdate = (startAge != null) ? now.minusYears(startAge) : null;
		final LocalDate minBirthdate = (endAge != null) ? now.minusYears(endAge + 1).plusDays(1) : null;

		// Tuple로 회원과 카테고리명을 함께 조회 (회원이 없는 경우에도 null을 포함할 수 있도록 left join)
		List<Tuple> results = queryFactory
			.select(
				member.id,
				member.profileImage,
				member.nickname,
				member.birthdate,
				member.gender,
				memberCategory.category.name)
			.from(member)
			.leftJoin(memberCategory).on(member.eq(memberCategory.member))
			.where(
				notSelf(memberId),
				categoryIn(categoryIds),
				genderEq(gender),
				birthdateFilter(minBirthdate, maxBirthdate)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(member.id.desc())
			.fetch();

		Map<Long, List<Tuple>> grouped = results.stream()
			.collect(Collectors.groupingBy(t -> t.get(member.id)));

		// 그룹별로 DTO 변환
		List<SearchMemberResponse> content = grouped.entrySet().stream().map(entry -> {
			List<Tuple> tuples = entry.getValue();
			// 그룹의 첫 번째 Tuple에서 회원 기본 정보 획득
			Tuple first = tuples.get(0);
			Long id = first.get(member.id);
			String profileImage = first.get(member.profileImage);
			String nickname = first.get(member.nickname);
			LocalDate birthdate = first.get(member.birthdate);
			Integer age = birthdate != null ? Period.between(birthdate, LocalDate.now()).getYears() : null;
			Gender memberGender = first.get(member.gender);
			// 그룹 내 모든 Tuple에서 카테고리명을 추출 (null 값은 제외) 후 CategoryName으로 매핑
			List<CategoryName> categories = tuples.stream()
				.map(t -> t.get(memberCategory.category.name))
				.filter(name -> name != null)
				.distinct()
				.map(CategoryName::new)
				.collect(Collectors.toList());
			return new SearchMemberResponse(id, profileImage, nickname, age, memberGender, categories);
		}).collect(Collectors.toList());

		long total = queryFactory
			.select(member.id)
			.from(member)
			.leftJoin(memberCategory).on(member.eq(memberCategory.member))
			.where(
				notSelf(memberId),
				categoryIn(categoryIds),
				genderEq(gender),
				birthdateFilter(minBirthdate, maxBirthdate)
			)
			.fetchCount();

		return new PageImpl<>(content, pageable, total);
	}

	private BooleanExpression notSelf(Long memberId) {
		return memberId != null ? member.id.ne(memberId) : null;
	}

	private BooleanExpression categoryIn(List<Long> categoryIds) {
		return (categoryIds != null && !categoryIds.isEmpty()) ? memberCategory.category.id.in(categoryIds) : null;
	}

	private BooleanExpression genderEq(Gender gender) {
		return gender != null ? member.gender.eq(gender) : null;
	}

	private BooleanExpression birthdateFilter(LocalDate minBirthdate, LocalDate maxBirthdate) {
		if (minBirthdate != null && maxBirthdate != null) {
			return member.birthdate.between(minBirthdate, maxBirthdate);
		} else if (maxBirthdate != null) {
			return member.birthdate.loe(maxBirthdate);
		} else if (minBirthdate != null) {
			return member.birthdate.goe(minBirthdate);
		}
		return null;
	}
}