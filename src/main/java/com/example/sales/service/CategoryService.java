package com.example.sales.service;

import com.example.sales.entity.Category;
import com.example.sales.entity.Product;
import com.example.sales.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    
    //Lấy danh sách tất cả danh mục sản phẩm
    public List<Category> getAllCategory(){
        return categoryRepository.findAllByOrderByIdAsc();
    }

    //Lấy danh mục sản phẩm theo id
    public Category getCategoryById(String id) {
        return categoryRepository.findById(id).orElseThrow(()-> new RuntimeException("Không tìm thấy danh mục sản phẩm!"));
    }

    // ID tự động thêm vào
    private String generateCategoryId() {
        List<String> ids = categoryRepository.findAllIdsDesc();
        if (ids.isEmpty()) return "CAT001";

        String lastId = ids.get(0);
        int num = Integer.parseInt(lastId.replace("CAT", ""));
        return String.format("CAT%03d", num + 1);
    }

    //Thêm một danh mục sản phẩm mới
    public Category addCategory(Category category) {
        category.setId(generateCategoryId());
        return categoryRepository.save(category);
    }

    //Cập nhật danh mục sản phẩm
    public Category updateCategory(String id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có ID: " + id));
        existingCategory.setName(category.getName());
        return categoryRepository.save(existingCategory);
    }

    // Xoá một danh mục sản phẩm
    public void deleteCategory(String id){
        if(!categoryRepository.existsById(id)){
            throw new RuntimeException("không tồn tại danh mục");
        }
        categoryRepository.deleteById(id);
    }
}
