package com.omarahmed42.catalog.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.omarahmed42.catalog.dto.request.CategoryCreation;
import com.omarahmed42.catalog.dto.request.PaginationRequest;
import com.omarahmed42.catalog.dto.response.CategoryResponse;
import com.omarahmed42.catalog.dto.response.PaginationResult;
import com.omarahmed42.catalog.enums.SortOrder;
import com.omarahmed42.catalog.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(value = "/categories")
    public ResponseEntity<Void> addCategory(@RequestBody CategoryCreation category) {
        CategoryResponse categoryResponse = categoryService.addCategory(category);
        return ResponseEntity.created(URI.create(categoryResponse.id().toString())).build();
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Integer id,
    @RequestBody CategoryCreation category) {
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }
    
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cateogries/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @GetMapping("/categories")
    public ResponseEntity<PaginationResult<CategoryResponse>> getCategories(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "15") Integer size, @RequestParam(defaultValue = "ASC") SortOrder sortOrder) {
        return ResponseEntity.ok(categoryService.getCategories(new PaginationRequest(page, size, sortOrder)));
    }

}
