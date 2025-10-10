package com.example.sales.service;

import com.example.sales.dto.ProductDTO;
import com.example.sales.entity.Product;
import com.example.sales.repository.ProductRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProduct() {

        return productRepository.findAll();

    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("không tồn tại sản phẩm!");
        }
        productRepository.deleteById(id);
    }

    // Tìm theo tên
    public List<Product> searchProductByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // Tìm theo danh mục
    public List<Product> searchByCategory(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }

    // Giảm số lượng khi đặt hàng
    public void reduceQuantity(Long productId, int amount) {
        Product p = getProductById(productId);
        if (p.getQuantity() < amount)
            throw new RuntimeException("Sản phẩm không đủ hàng!");
        p.setQuantity(p.getQuantity() - amount);
        productRepository.save(p);
    }
}
