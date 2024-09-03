package com.omarahmed42.cart.dto.response;

import java.util.List;

public record PaginationResult<T>(
        List<T> content,
        int numberOfElements,
        long totalElements,
        int totalPages,
        int currentPageNumber) {
}
