package com.example.sales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sales.entity.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}