package com.example.sales.service;

import com.example.sales.dto.ProductCreationDTO;
import com.example.sales.dto.ProductUpdateDTO;
import com.example.sales.entity.Category;
import com.example.sales.entity.Product;
import com.example.sales.repository.CategoryRepository;
import com.example.sales.repository.ProductRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.transaction.Transactional;
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
        return productRepository.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
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
    @Transactional
    public Product addProduct(ProductCreationDTO productDTO) {
        // 1. Tìm Category từ database dựa trên categoryName từ DTO
        Category category = categoryRepository.findByNameIgnoreCase(productDTO.getCategoryName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục: " + productDTO.getCategoryName()));

        // 2. Tạo một đối tượng Product mới (trong bộ nhớ Java)
        Product newProduct = new Product();
        newProduct.setId(generateProductId());
        newProduct.setName(productDTO.getName());
        newProduct.setPrice(productDTO.getPrice());
        newProduct.setQuantity(productDTO.getQuantity());
        newProduct.setDescription(productDTO.getDescription());
        newProduct.setCategory(category); // Gán đối tượng Category

        productRepository.insertProduct(
                newProduct.getId(),
                newProduct.getName(),
                newProduct.getPrice(),
                newProduct.getQuantity(),
                newProduct.getDescription(),
                newProduct.getCategory().getId()
        );

        return newProduct;
    }

    // Cập nhật sản phẩm
    @Transactional
    public Product updateProduct(String id, ProductUpdateDTO productDetails) {
        // 1. Lấy sản phẩm hiện có từ DB
        Product existingProduct = getProductById(id);

        // 2. Cập nhật các trường trên đối tượng Java
        existingProduct.setName(productDetails.getName());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setQuantity(productDetails.getQuantity());
        existingProduct.setDescription(productDetails.getDescription());

        // 3. Xử lý cập nhật Category
        if (productDetails.getCategoryName() != null && !productDetails.getCategoryName().isEmpty()) {
            Category newCategory = categoryRepository.findByNameIgnoreCase(productDetails.getCategoryName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục: " + productDetails.getCategoryName()));
            existingProduct.setCategory(newCategory);
        }

        productRepository.updateProduct(
                existingProduct.getId(),
                existingProduct.getName(),
                existingProduct.getPrice(),
                existingProduct.getQuantity(),
                existingProduct.getDescription(),
                existingProduct.getCategory().getId()
        );

        return existingProduct;
    }

    // Xoá một sản phẩm theo id
    @Transactional
    public void deleteProduct(String id) {
        if (productRepository.countById(id) == 0) {
            throw new RuntimeException("không tồn tại sản phẩm!");
        }
        productRepository.deleteProductById(id);
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
