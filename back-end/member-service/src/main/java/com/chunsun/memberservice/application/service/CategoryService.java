package com.chunsun.memberservice.application.service;

import java.util.List;

import com.chunsun.memberservice.application.dto.CategoryDto;
import com.chunsun.memberservice.domain.Entity.Category;

public interface CategoryService {

	List<Category> getList();

	List<Category> getUserCategories(Long id);

	void createCategory(Long id, CategoryDto.CategoryRequest request);

	void updateCategory(Long id, List<Long> categoryIds);
}
