package com.example.sales.repository;

import com.example.sales.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sales.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {


    //Lấy 1 sản phẩm theo id của sản phẩm
    @Query(value = "SELECT * FROM Product p WHERE p.product_id = :productId", nativeQuery = true)
    Optional<Product> findById(@Param("productId") String productId);

    //Lấy tất cả danh sách Product
    @Query(value = "SELECT * FROM Product", nativeQuery = true)
    List<Product> getAllProduct();

    //Tự động tạo id tăng dần
    @Query(value = "SELECT product_id From Product ORDER BY CAST(SUBSTRING(product_id, 3) AS UNSIGNED DESC", nativeQuery = true)
    List<String> findAllIdsDesc();

    // Kiểm tra tồn tại sản phẩm
    @Query(value = "SELECT COUNT(*) > 0 FROM Product WHERE product_id = :productId", nativeQuery = true)
    boolean existsById(@Param("productId") String productId);

    // Xóa sản phẩm theo ID
    @Modifying
    @Query(value = "DELETE FROM Product WHERE product_id = :productId", nativeQuery = true)
    void deleteById(@Param("productId") String productId);

    // Tìm theo tên
    @Query(value = "SELECT * FROM Product p WHERE LOWER(p.name) LIKE CONCAT('%', LOWER(:keyword), '%')", nativeQuery = true)
    List<Product> findByNameContainingIgnoreCase(@Param("keyword") String keyword);

    // Tìm sản phẩm theo tên danh mục
    @Query(value = "SELECT * FROM Product p JOIN Category c ON p.category_id = c.category_id WHERE LOWER(c.name) = LOWER(:categoryName)", nativeQuery = true)
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);

    // Tìm sản phẩm có giá nhỏ hơn price
    @Query(value = "SELECT * FROM Product p WHERE p.price < :price", nativeQuery = true)
    List<Product> findByPriceLessThan(@Param("price") double price);

    // Tìm sản phẩm có giá trong khoảng min đến max
    @Query(value = "SELECT * FROM Product p WHERE p.price BETWEEN :min AND :max", nativeQuery = true)
    List<Product> findByPriceBetween(@Param("min") double min, @Param("max") double max);


}