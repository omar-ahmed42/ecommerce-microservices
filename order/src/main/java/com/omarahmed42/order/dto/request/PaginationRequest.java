package com.omarahmed42.order.dto.request;

import com.omarahmed42.order.enums.SortOrder;

public record PaginationRequest(Integer page, Integer size, SortOrder sortOrder) {
}
