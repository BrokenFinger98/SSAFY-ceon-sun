package com.chunsun.memberservice.application.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.memberservice.application.dto.CategoryDto;
import com.chunsun.memberservice.common.error.GlobalErrorCodes;
import com.chunsun.memberservice.common.exception.BusinessException;
import com.chunsun.memberservice.domain.Entity.Category;
import com.chunsun.memberservice.domain.Repository.CategoryRepository;
import com.chunsun.memberservice.domain.Entity.Member;
import com.chunsun.memberservice.domain.Entity.MemberCategory;
import com.chunsun.memberservice.domain.Repository.MemberCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

	private final MemberCategoryRepository memberCategoryRepository;
	private final CategoryRepository categoryRepository;

	@Override
	public List<Category> getList() {

		List<Category> categories = (List<Category>)categoryRepository.findAll();

		return categories;
	}

	@Override
	public List<Category> getUserCategories(Long memberId) {

		List<Long> categoryIds = memberCategoryRepository.findByMemberId(memberId)
			.stream()
			.map(mc -> mc.getCategory().getId())
			.collect(Collectors.toList());

		if(categoryIds.isEmpty()) {
			return Collections.emptyList();
		}

		return categoryRepository.findByIdIn(categoryIds);
	}

	@Override
	@Transactional
	public void createCategory(CategoryDto.CategoryRequest request) {

		for (Long categoryId : request.categoryIds()) {
			Category category = categoryRepository.findById(categoryId).orElseThrow(()
				-> new BusinessException(GlobalErrorCodes.CATEGORY_NOT_FOUND));

			MemberCategory memberCategory = new MemberCategory(new Member(request.id()), category);
			memberCategoryRepository.save(memberCategory);
		}
	}

	@Override
	@Transactional
	public void deleteCategory(Long memberId) {

		memberCategoryRepository.deleteByMemberId(memberId);
	}
}
