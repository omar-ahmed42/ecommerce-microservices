package com.omarahmed42.catalog.dto.response;

public record CategoryResponse(
        Integer id,
        String name,
        String description,
        Integer parentCategoryId) {
}
