package com.omarahmed42.catalog.service;

import com.omarahmed42.catalog.dto.request.CategoryCreation;
import com.omarahmed42.catalog.dto.request.PaginationRequest;
import com.omarahmed42.catalog.dto.response.CategoryResponse;
import com.omarahmed42.catalog.dto.response.PaginationResult;

public interface CategoryService {

    CategoryResponse addCategory(CategoryCreation categoryCreation);
    CategoryResponse updateCategory(Integer categoryId, CategoryCreation categoryCreation);
    void deleteCategory(Integer id);
    CategoryResponse getCategory(Integer id);
    PaginationResult<CategoryResponse> getCategories(PaginationRequest paginationRequest);

}
