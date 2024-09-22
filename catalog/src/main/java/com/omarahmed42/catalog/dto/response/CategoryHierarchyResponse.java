package com.omarahmed42.catalog.dto.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class CategoryHierarchyResponse implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private CategoryHierarchyResponse parentCategory;
}
