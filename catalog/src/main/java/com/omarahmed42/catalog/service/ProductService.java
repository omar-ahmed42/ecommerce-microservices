package com.omarahmed42.catalog.service;

import java.util.List;

import com.omarahmed42.catalog.dto.request.ProductCreation;
import com.omarahmed42.catalog.dto.request.QueryFilter;
import com.omarahmed42.catalog.dto.response.PaginationResult;
import com.omarahmed42.catalog.dto.response.ProductResponse;
import com.omarahmed42.catalog.message.payload.item.PricedItem;

public interface ProductService {
    ProductResponse addProduct(ProductCreation product);

    ProductResponse getProduct(Long id);

    PaginationResult<ProductResponse> getProducts(QueryFilter filter);
    
    List<PricedItem> getPricedProducts(List<Long> productsIds);
}
