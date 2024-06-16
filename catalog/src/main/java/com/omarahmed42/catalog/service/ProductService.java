package com.omarahmed42.catalog.service;

import com.omarahmed42.catalog.dto.request.ProductCreation;
import com.omarahmed42.catalog.dto.response.ProductResponse;

public interface ProductService {
    ProductResponse addProduct(ProductCreation product);
}
