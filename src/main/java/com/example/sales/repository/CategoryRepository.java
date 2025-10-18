package com.example.sales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sales.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query("SELECT c.id FROM Category c ORDER BY c.id DESC")
    List<String> findAllIdsDesc();

    @Query(value = "SELECT * FROM Category c WHERE LOWER(c.name) = LOWER(:name)", nativeQuery = true)
    Optional<Category> findByNameIgnoreCase(@Param("name") String name);
}