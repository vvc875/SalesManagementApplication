package com.example.sales.controller;

import com.example.sales.dto.ProductDTO;
import com.example.sales.entity.Product;
import com.example.sales.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    List<Product> getProduct(){
        return productService.getAllProduct();
    }

//    @GetMapping("/{productId}")
//    Product getProductById(@PathVariable("productId") Long productId){
//        return  productService.getProductById(productId);
//    }

    @GetMapping("/search")
    public List<Product> searchProductByName(@RequestParam("keyword") String keyword) {
        return productService.searchProductByName(keyword);
    }

    @GetMapping("/category")
    public List<Product> searchByCategory(@RequestParam("name") String categoryName) {
        return productService.searchByCategory(categoryName);
    }

    @PostMapping
    Product addProduct(@RequestBody Product product){
        return productService.addProduct(product);
    }

    @PutMapping("/{productId}")
    Product updateProduct(@RequestBody Product product, @PathVariable("productId") Long productId){
        return productService.updateProduct(productId, product);
    }

    @DeleteMapping("/{productId}")
    String  deleteProduct(@PathVariable("productId") Long productId){
        productService.deleteProduct(productId);
        return "delete product successfully";
    }
}
