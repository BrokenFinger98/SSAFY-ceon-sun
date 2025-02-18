package com.chunsun.memberservice.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.memberservice.application.dto.CategoryDto;
import com.chunsun.memberservice.domain.Entity.Category;
import com.chunsun.memberservice.domain.Repository.CategoryRepository;
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

		return (List<Category>)categoryRepository.findAll();
	}

	@Override
	public List<Category> getUserCategories(Long id) {

		return categoryRepository.findCategoriesByMemberId(id);
	}

	@Override
	@Transactional
	public void createCategory(Long id, CategoryDto.CategoryRequest request) {

		memberCategoryRepository.insertMemberCategory(id, request.categoryIds());
	}


	@Override
	@Transactional
	public void updateCategory(Long id, List<Long> categoryIds) {

		memberCategoryRepository.deleteByMemberId(id);

		memberCategoryRepository.insertMemberCategory(id, categoryIds);
	}
}
