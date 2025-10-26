package com.example.sales.repository;

import com.example.sales.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query(value = "SELECT * FROM Category ORDER BY category_id ASC", nativeQuery = true)
    List<Category> getAllCategory();

    @Query(value = "SELECT * FROM Category WHERE category_id = :id", nativeQuery = true)
    Optional<Category> getCategoryById(@Param("id") String id);

    @Query(value = "SELECT * FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :nameKeyword, '%'))", nativeQuery = true)
    List<Category> getCategoryByName(@Param("nameKeyword") String nameKeyword);

    @Query(value = "SELECT COUNT(*) FROM Category WHERE category_id = :id", nativeQuery = true)
    int countByIdNative(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM Category WHERE category_id = :id", nativeQuery = true)
    void deleteByIdNative(@Param("id") String id);

    @Modifying
    @Query(value = "INSERT INTO Category (category_id, name) VALUES (:id, :name)", nativeQuery = true)
    void insertCategory(@Param("id") String id, @Param("name") String name);

    @Modifying
    @Query(value = "UPDATE Category SET name = :name WHERE category_id = :id", nativeQuery = true)
    void updateCategory(@Param("id") String id, @Param("name") String name);

    @Query(value = "SELECT category_id FROM Category ORDER BY CAST(SUBSTRING(category_id, 4) AS UNSIGNED) DESC", nativeQuery = true)
    List<String> findAllIdsDesc();

    @Query(value = "SELECT * FROM Category c WHERE LOWER(c.name) = LOWER(:name)", nativeQuery = true)
    Optional<Category> findByNameIgnoreCase(@Param("name") String name);
}