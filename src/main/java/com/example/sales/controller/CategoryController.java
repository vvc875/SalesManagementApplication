package com.example.sales.controller;

import com.example.sales.entity.Category;
import com.example.sales.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    List<Category> getCategory(){
        return categoryService.getAllCategory();
    }

    @GetMapping("/{categoryId}")
    Category getCategoryById(@PathVariable("categoryId") Long categoryId){
        return categoryService.getCategoryById(categoryId);
    }

    @PostMapping
    public  Category addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

    @PutMapping("/{categoryId}")
    Category updateCategory(@RequestBody Category category,  @PathVariable("categoryId") Long categoryId){
        return categoryService.updateCategory(categoryId, category);
    }

    @DeleteMapping("/{categoryId}")
    String deleteCategory(@PathVariable("categoryId") Long categoryId){
        categoryService.deleteCategory(categoryId);
        return "delete category successfully";
    }
}
