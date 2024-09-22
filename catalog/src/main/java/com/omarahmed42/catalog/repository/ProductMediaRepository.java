package com.omarahmed42.catalog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omarahmed42.catalog.model.Product;
import com.omarahmed42.catalog.model.ProductMedia;

@Repository
public interface ProductMediaRepository extends JpaRepository<ProductMedia, Long> {
    List<ProductMedia> findAllByProduct(Product product);
    long countByProduct(Product product);
}
