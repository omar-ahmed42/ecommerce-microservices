package com.omarahmed42.catalog.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omarahmed42.catalog.dto.request.ProductCreation;
import com.omarahmed42.catalog.dto.response.ProductResponse;
import com.omarahmed42.catalog.mapper.ProductMapper;
import com.omarahmed42.catalog.model.Product;
import com.omarahmed42.catalog.repository.ProductRepository;
import com.omarahmed42.catalog.service.ProductService;
import com.omarahmed42.catalog.util.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse addProduct(@Valid ProductCreation productCreation) {
        Product product = productMapper.toEntity(productCreation);
        product.setSellerId(SecurityUtils.getAuthenticatedUserId());
        product = productRepository.save(product);

        return productMapper.toProductResponse(product);
    }
}