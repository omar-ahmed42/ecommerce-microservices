package com.omarahmed42.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omarahmed42.catalog.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
