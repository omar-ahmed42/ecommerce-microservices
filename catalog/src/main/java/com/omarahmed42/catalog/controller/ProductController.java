package com.omarahmed42.catalog.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.omarahmed42.catalog.dto.request.ProductCreation;
import com.omarahmed42.catalog.dto.request.QueryFilter;
import com.omarahmed42.catalog.dto.response.CategoryHierarchyResponse;
import com.omarahmed42.catalog.dto.response.PaginationResult;
import com.omarahmed42.catalog.dto.response.ProductMediaResponse;
import com.omarahmed42.catalog.dto.response.ProductResponse;
import com.omarahmed42.catalog.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/products")
    public ResponseEntity<Void> addProduct(@RequestBody @Valid ProductCreation product) {
        ProductResponse response = productService.addProduct(product);
        return ResponseEntity.created(URI.create(response.id().toString())).build();
    }

    @GetMapping(value = "/products/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping(value = "/products")
    public ResponseEntity<PaginationResult<ProductResponse>> getProducts(QueryFilter filter) {
        return ResponseEntity.ok(productService.getProducts(filter));
    }

    @GetMapping(value = "/products/{id}/categories/hierarchy")
    public ResponseEntity<CategoryHierarchyResponse> getProductCategoryHierarchy(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProductCategoryHierarchy(id));
    }

    @PostMapping(value = "/products/{id}/thumbnail")
    public ResponseEntity<Long> uploadThumbnail(@PathVariable("id") Long id, MultipartFile file) {
        return ResponseEntity.ok(productService.saveThumbnail(file, id));
    }

    @PostMapping(value = "/products/{id}/media")
    public ResponseEntity<Long> uploadProductMedia(@PathVariable("id") Long id, MultipartFile file) {
        return ResponseEntity.ok(productService.saveProductMedia(id, file));
    }
    
    @GetMapping(value = "/products/{id}/media")
    public ResponseEntity<List<ProductMediaResponse>> getProductMedia(@PathVariable("id") Long productId){
        return ResponseEntity.ok(productService.getProductMedia(productId));
    }
}
