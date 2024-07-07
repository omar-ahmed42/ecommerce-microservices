package com.omarahmed42.catalog.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
import com.omarahmed42.catalog.message.payload.item.PricedItem;
import com.omarahmed42.catalog.message.producer.MessageSender;
import com.omarahmed42.catalog.model.Product;
import com.omarahmed42.catalog.repository.ProductRepository;
import com.omarahmed42.catalog.service.ProductService;
import com.omarahmed42.catalog.utils.PageUtils;
import com.omarahmed42.catalog.utils.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MessageSender messageSender;

    @Override
    @Transactional
    public ProductResponse addProduct(@Valid ProductCreation productCreation) {
        log.info("Creating a product with name {} and description {} and price {} and category id {}",
                productCreation.name(), productCreation.description(), productCreation.price(),
                productCreation.categoryId());
        Product product = productMapper.toEntity(productCreation);
        String subject = SecurityUtils.getSubject();
        log.info("Subject {}", subject);
        product.setSellerId(subject);
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
            Page<Product> productsPage = productRepository.findByNameContaining(filter.getSearch(), page);
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

    @Override
    public List<PricedItem> getPricedProducts(List<Long> productsIds) {
        if (productsIds == null || productsIds.isEmpty())
            return Collections.emptyList();

        List<Long> filteredProductIds = productsIds.stream().filter(Objects::nonNull).toList();
        if (filteredProductIds.isEmpty())
            return Collections.emptyList();

        List<Product> pricedProducts = productRepository.findAllIdAndPriceByIdIn(filteredProductIds);
        return productMapper.toPricedItemList(pricedProducts);
    }

}