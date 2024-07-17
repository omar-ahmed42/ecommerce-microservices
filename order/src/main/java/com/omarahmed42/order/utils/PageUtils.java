package com.omarahmed42.order.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.omarahmed42.order.dto.request.PaginationRequest;
import com.omarahmed42.order.dto.request.QueryFilter;
import com.omarahmed42.order.model.Order_;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PageUtils {

    public static PageRequest getPage(PaginationRequest paginationRequest) {
        if (paginationRequest.sortOrder() == null) {
            return PageRequest.of(paginationRequest.page() - 1, paginationRequest.size());
        }

        return PageRequest.of(paginationRequest.page() - 1, paginationRequest.size(),
                Sort.by(
                        Direction.valueOf(paginationRequest.sortOrder().toString()), Order_.ID));
    }

    public static PageRequest getPages(QueryFilter queryFilter) {
        if (queryFilter.getSortOrder() == null) {
            return PageRequest.of(queryFilter.getPage() - 1, queryFilter.getSize());
        }

        return PageRequest.of(queryFilter.getPage() - 1, queryFilter.getSize(),
                Sort.by(
                        Direction.valueOf(queryFilter.getSortOrder().toString()), Order_.ID));
    }
}
