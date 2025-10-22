package com.example.sales.service;

import com.example.sales.entity.Category;
import com.example.sales.entity.Product;
import com.example.sales.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    //Lấy tất cả danh mục sản phẩm
    public List<Category> getAllCategory(){
        return categoryRepository.getAllCategory();
    }

    //Lấy danh mục sản phẩm theo id
    public Category getCategoryById(String id) {
        return categoryRepository.getCategoryById(id).orElseThrow(()-> new RuntimeException("Không tìm thấy danh mục sản phẩm!"));
    }

    // ID tự động thêm vào
    private String generateCategoryId() {
        List<String> ids = categoryRepository.findAllIdsDesc();
        if (ids.isEmpty()) return "CAT001";

        String lastId = ids.get(0);
        int num = Integer.parseInt(lastId.replace("CAT", ""));
        return String.format("CAT%02d", num + 1);
    }

    //Thêm một danh mục sản phẩm mới
    @Transactional
    public Category addCategory(Category category) {
        category.setId(generateCategoryId());
        categoryRepository.insertCategory(category.getId(), category.getName());
        return category;
    }

    //Cập nhật danh mục sản phẩm
    @Transactional
    public Category updateCategory(String id, Category category) {
        Category existingCategory = categoryRepository.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có ID: " + id));

        categoryRepository.updateCategory(id, category.getName());

        existingCategory.setName(category.getName());
        return existingCategory;
    }

    // Xoá một danh mục sản phẩm
    @Transactional //
    public void deleteCategory(String id){
        if(categoryRepository.countByIdNative(id) == 0){
            throw new RuntimeException("không tồn tại danh mục");
        }
        categoryRepository.deleteByIdNative(id);
    }
}