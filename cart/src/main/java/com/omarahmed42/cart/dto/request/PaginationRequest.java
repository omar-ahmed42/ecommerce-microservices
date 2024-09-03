package com.omarahmed42.cart.dto.request;

import com.omarahmed42.cart.enums.SortOrder;

public record PaginationRequest(Integer page, Integer size, SortOrder sortOrder) {
}
