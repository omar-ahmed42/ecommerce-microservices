package com.omarahmed42.catalog.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omarahmed42.catalog.dto.request.ProductCreation;
import com.omarahmed42.catalog.dto.response.ProductResponse;
import com.omarahmed42.catalog.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<Void> addProduct(@RequestBody @Valid ProductCreation product) {
        ProductResponse response = productService.addProduct(product);
        return ResponseEntity.created(URI.create(response.id().toString())).build();
    }

}
