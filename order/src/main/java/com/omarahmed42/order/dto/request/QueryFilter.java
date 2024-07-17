package com.omarahmed42.order.dto.request;

import com.omarahmed42.order.enums.SortOrder;

import lombok.Data;

@Data
public class QueryFilter {
    private Integer page = 1;
    private Integer size = 15;
    private String search;
    private SortOrder sortOrder = SortOrder.DESC;
}
