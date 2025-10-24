package com.example.sales.controller;

import com.example.sales.dto.ProductCreationDTO;
import com.example.sales.dto.ProductUpdateDTO;
import com.example.sales.entity.Product;
import com.example.sales.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    List<Product> getProduct(){
        return productService.getAllProduct();
    }

    @GetMapping("/{productId}")
    Product getProductById(@PathVariable("productId") String productId){
        return productService.getProductById(productId);
    }

    @GetMapping("/name")
    public List<Product> searchProductByName(@RequestParam("keyword") String keyword) {
        return productService.searchProductByName(keyword);
    }

    @GetMapping("/category")
    public List<Product> searchByCategory(@RequestParam("keyword") String categoryName) {
        return productService.searchByCategory(categoryName);
    }

    @PostMapping
    Product addProduct(@RequestBody ProductCreationDTO productDTO){

        return productService.addProduct(productDTO);
    }

    @PutMapping("/{productId}")
    Product updateProduct(@RequestBody ProductUpdateDTO productUpdateDTO, @PathVariable("productId") String productId){
        return productService.updateProduct(productId, productUpdateDTO);
    }

    @DeleteMapping("/{productId}")
    String  deleteProduct(@PathVariable("productId") String productId){
        productService.deleteProduct(productId);
        return "Delete product successfully";
    }

    @GetMapping("/price/less")
    List<Product> getByLessThan(@RequestParam double max){
        return productService.getByPriceLessThan(max);
    }

    @GetMapping("/price/between")
    List<Product> getByBetween(@RequestParam double min, @RequestParam double max){
        return productService.getByPriceBetween(min,max);
    }
}