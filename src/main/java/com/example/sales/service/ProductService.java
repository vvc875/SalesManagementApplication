package com.example.sales.service;

import com.example.sales.dto.ProductCreationDTO;
import com.example.sales.dto.ProductUpdateDTO;
import com.example.sales.entity.Category;
import com.example.sales.entity.Product;
import com.example.sales.repository.CategoryRepository;
import com.example.sales.repository.ProductRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    //Lấy tất cả danh sách Product
    public List<Product> getAllProduct() {
        return productRepository.getAllProduct();
    }

    //Lấy 1 sản phẩm theo id của sản phẩm
    public Product getProductById(String id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
    }

    //Tự động tạo id tăng dần
    private String generateProductId() {
        List<String> ids = productRepository.findAllIdsDesc();
        if (ids.isEmpty()) return "PR001";

        String lastId = ids.get(0);
        int num = Integer.parseInt(lastId.replace("PR", ""));
        return String.format("PR%03d", num + 1);
    }

    //Thêm một sản phẩm mới
    public Product addProduct(ProductCreationDTO productDTO) {
        // 1. Tìm Category từ database dựa trên categoryName từ DTO
        Category category = categoryRepository.findByNameIgnoreCase(productDTO.getCategoryName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + productDTO.getCategoryName()));

        // 2. Tạo một đối tượng Product mới
        Product newProduct = new Product();
        newProduct.setId(generateProductId()); // Tự tạo ID
        newProduct.setName(productDTO.getName());
        newProduct.setPrice(productDTO.getPrice());
        newProduct.setQuantity(productDTO.getQuantity());
        newProduct.setDescription(productDTO.getDescription());

        // 3. Gán đối tượng Category đã được quản lý vào sản phẩm mới
        newProduct.setCategory(category);

        // 4. Lưu sản phẩm mới vào database
        return productRepository.save(newProduct);
    }

    //cập nhật sản phẩm
    public Product updateProduct(String id, ProductUpdateDTO productDetails) {
        // 1. Lấy sản phẩm hiện có từ DB
        Product existingProduct = getProductById(id);

        // 2. Cập nhật các trường thông thường
        existingProduct.setName(productDetails.getName());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setQuantity(productDetails.getQuantity());
        existingProduct.setDescription(productDetails.getDescription());

        // 3. Xử lý cập nhật Category một cách an toàn
        // Chỉ thay đổi category nếu client cung cấp categoryName mới
        if (productDetails.getCategoryName() != null && !productDetails.getCategoryName().isEmpty()) {
            Category newCategory = categoryRepository.findByNameIgnoreCase(productDetails.getCategoryName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + productDetails.getCategoryName()));
            existingProduct.setCategory(newCategory);
        }

        // 4. Lưu lại sản phẩm đã cập nhật
        return productRepository.save(existingProduct);
    }

    //xoá một sản phẩm theo id
    public void deleteProduct(String id) {
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

    //Tìm sản phẩm theo giá lớn nhất
    public List<Product> getByPriceLessThan(double price) {
        return productRepository.findByPriceLessThan(price);
    }

    //Tìm sản phẩm ở giữa hai mức giá
    public List<Product> getByPriceBetween(double min, double max) {
        return productRepository.findByPriceBetween(min, max);
    }

}
