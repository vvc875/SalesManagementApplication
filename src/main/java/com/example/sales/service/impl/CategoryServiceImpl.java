package com.example.sales.service.impl;
import com.example.sales.entity.Category;
import com.example.sales.repository.CategoryRepository;
import com.example.sales.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}