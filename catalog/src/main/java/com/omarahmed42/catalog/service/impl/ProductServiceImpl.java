package com.omarahmed42.catalog.service.impl;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omarahmed42.catalog.dto.message.Message;
import com.omarahmed42.catalog.dto.request.ProductCreation;
import com.omarahmed42.catalog.dto.request.QueryFilter;
import com.omarahmed42.catalog.dto.response.PaginationResult;
import com.omarahmed42.catalog.dto.response.ProductResponse;
import com.omarahmed42.catalog.exception.InvalidInputException;
import com.omarahmed42.catalog.exception.ProductNotFoundException;
import com.omarahmed42.catalog.mapper.ProductMapper;
import com.omarahmed42.catalog.message.payload.ProductCreatedPayload;
import com.omarahmed42.catalog.message.producer.MessageSender;
import com.omarahmed42.catalog.model.Product;
import com.omarahmed42.catalog.repository.ProductRepository;
import com.omarahmed42.catalog.service.ProductService;
import com.omarahmed42.catalog.util.PageUtils;
import com.omarahmed42.catalog.util.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MessageSender messageSender;

    @Override
    @Transactional
    public ProductResponse addProduct(@Valid ProductCreation productCreation) {
        Product product = productMapper.toEntity(productCreation);
        product.setSellerId(SecurityUtils.getAuthenticatedUserId());
        product = productRepository.save(product);

        emitProductCreatedEvent(product);
        return productMapper.toProductResponse(product);
    }

    private void emitProductCreatedEvent(Product product) {
        ProductCreatedPayload payload = new ProductCreatedPayload();
        payload.setProductId(product.getId());
        payload.setStock(0);
        payload.setCorrelationId(UUID.randomUUID().toString());

        Message<ProductCreatedPayload> message = new Message<>("ProductCreatedEvent", payload);
        messageSender.send(message);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        if (id == null)
            throw new InvalidInputException("Product ID cannot be empty");
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResult<ProductResponse> getProducts(QueryFilter filter) {
        PageRequest page = PageUtils.getPages(filter);

        if (StringUtils.isNotBlank((filter.getSearch()))) {
            // TODO: Test search performance using SQL and using Elasticsearch
            Page<Product> productsPage = productRepository.findByTitleContaining(filter.getSearch(), page);
            return toProductResponsePage(productsPage);
        }

        Page<Product> productsPage = productRepository.findAll(page);
        return toProductResponsePage(productsPage);
    }

    private PaginationResult<ProductResponse> toProductResponsePage(Page<Product> productsPage) {
        return new PaginationResult<>(productMapper.toProductResponseList(productsPage.getContent()),
                productsPage.getNumberOfElements(),
                productsPage.getTotalElements(),
                productsPage.getTotalPages(),
                productsPage.getNumber());

    }

}