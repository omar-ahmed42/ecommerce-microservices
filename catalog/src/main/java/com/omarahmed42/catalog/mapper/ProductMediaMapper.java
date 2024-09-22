package com.omarahmed42.catalog.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.omarahmed42.catalog.dto.response.ProductMediaResponse;
import com.omarahmed42.catalog.model.ProductMedia;

@Mapper(componentModel = "spring")
public interface ProductMediaMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "url", source = "attachment.url")
    ProductMediaResponse toProductMediaResponse(ProductMedia productMedia);

    List<ProductMediaResponse> toProductMediaResponseList(List<ProductMedia> productMedia);
}
