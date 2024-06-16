package com.omarahmed42.catalog.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omarahmed42.catalog.dto.request.CategoryCreation;
import com.omarahmed42.catalog.dto.request.PaginationRequest;
import com.omarahmed42.catalog.dto.response.CategoryResponse;
import com.omarahmed42.catalog.dto.response.PaginationResult;
import com.omarahmed42.catalog.exception.CategoryNotFoundException;
import com.omarahmed42.catalog.mapper.CategoryMapper;
import com.omarahmed42.catalog.model.Category;
import com.omarahmed42.catalog.repository.CategoryRepository;
import com.omarahmed42.catalog.service.CategoryService;
import com.omarahmed42.catalog.util.PageUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse addCategory(CategoryCreation categoryCreation) {
        log.info("Creating a new category");
        Category category = categoryMapper.toEntity(categoryCreation);
        category = categoryRepository.save(category);
        log.info("Category created successfully");
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Integer categoryId, CategoryCreation categoryCreation) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
        categoryMapper.toTargetEntity(category, categoryCreation);
        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategory(Integer id) {
        log.info("Retrieving category {}", id);
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        log.info("Retrieved category {}", id);
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResult<CategoryResponse> getCategories(PaginationRequest paginationRequest) {
        Page<Category> page = categoryRepository.findAll(PageUtils.getPage(paginationRequest));
        return new PaginationResult<>(categoryMapper.toCategoryResponses(page.getContent()),
                page.getNumberOfElements(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber());
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}
