package com.omarahmed42.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omarahmed42.catalog.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
}
