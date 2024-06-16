package com.omarahmed42.catalog.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import com.omarahmed42.catalog.dto.request.PaginationRequest;
import com.omarahmed42.catalog.dto.request.QueryFilter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PageUtils {

    public static PageRequest getPage(PaginationRequest paginationRequest) {
        if (paginationRequest.sortOrder() == null) {
            return PageRequest.of(paginationRequest.page() - 1, paginationRequest.size());
        }

        return PageRequest.of(paginationRequest.page() - 1, paginationRequest.size(),
                Direction.valueOf(paginationRequest.sortOrder().toString()));
    }

    public static PageRequest getPages(QueryFilter queryFilter) {
        if (queryFilter.getOrder() == null) {
            return PageRequest.of(queryFilter.getPage() - 1, queryFilter.getSize());
        }

        return PageRequest.of(queryFilter.getPage() - 1, queryFilter.getSize(),
                Direction.valueOf(queryFilter.getOrder().toString()));
    }
}
