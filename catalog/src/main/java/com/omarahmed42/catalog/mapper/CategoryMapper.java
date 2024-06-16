package com.omarahmed42.catalog.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.omarahmed42.catalog.dto.request.CategoryCreation;
import com.omarahmed42.catalog.dto.response.CategoryResponse;
import com.omarahmed42.catalog.model.Category;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    @Mapping(target = "parentCategory.id", source = "parentCategoryId")
    Category toEntity(CategoryCreation categoryCreation);

    @Mapping(target = "parentCategory.id", source = "parentCategoryId")
    Collection<Category> fromCategoryCategories(Collection<CategoryCreation> categoryCreation);

    @Mapping(target = "parentCategory.id", source = "parentCategoryId")
    Category toEntity(CategoryResponse categoryResponse);
    
    @Mapping(target = "parentCategory.id", source = "parentCategoryId")
    Collection<Category> fromCategoryResponses(Collection<CategoryResponse> categoryResponse);

    @InheritInverseConfiguration(name = "toEntity")
    CategoryCreation toCategoryCreation(Category category);

    @InheritInverseConfiguration
    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "parentCategory.id", source = "parentCategoryId")
    void toTargetEntity(@MappingTarget Category category, CategoryCreation categoryCreation);

    List<CategoryResponse> toCategoryResponses(Collection<Category> categories);

}
