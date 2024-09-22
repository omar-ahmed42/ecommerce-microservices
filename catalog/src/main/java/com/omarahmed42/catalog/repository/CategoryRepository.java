package com.omarahmed42.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.omarahmed42.catalog.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query(nativeQuery = true, value = """
                        WITH RECURSIVE category_tree (id, name, description, parent_category_id) AS (
                SELECT id, name, description, parent_category_id
                FROM categories
                WHERE id = :category_id

                UNION ALL

                SELECT tt.id, tt.name, tt.description, tt.parent_category_id
                FROM categories tt
                  JOIN category_tree tr ON tr.id = tt.parent_category_id
            )
            SELECT *
            FROM category_tree
                        """)
    Category findCategoryHierarchy(@Param("category_id") Integer id);
}
