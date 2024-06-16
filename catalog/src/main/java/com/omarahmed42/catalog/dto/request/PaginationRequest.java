package com.omarahmed42.catalog.dto.request;

import com.omarahmed42.catalog.enums.SortOrder;

public record PaginationRequest(Integer page, Integer size, SortOrder sortOrder) {
}
