package com.example.sales.repository;

import com.example.sales.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    // SỬA: Bỏ JOIN. JPA sẽ tự xử lý mapping object Category khi cần.
    @Query(value = "SELECT * FROM Product", nativeQuery = true)
    List<Product> getAllProduct();

    // SỬA: Bỏ JOIN.
    @Query(value = "SELECT * FROM Product WHERE product_id = :id", nativeQuery = true)
    Optional<Product> getProductById(@Param("id") String id);

    @Modifying
    @Query(value = """
            INSERT INTO Product (product_id, name, price, quantity, description, category_id)
            VALUES (:id, :name, :price, :quantity, :description, :categoryId)
            """, nativeQuery = true)
    void insertProduct(@Param("id") String id,
                       @Param("name") String name,
                       @Param("price") double price,
                       @Param("quantity") int quantity,
                       @Param("description") String description,
                       @Param("categoryId") String categoryId);

    @Modifying
    @Query(value = "UPDATE Product SET " +
            "name = :name, " +
            "price = :price, " +
            "quantity = :quantity, " +
            "description = :description, " +
            "category_id = :categoryId " +
            "WHERE product_id = :id", nativeQuery = true)
    void updateProduct(@Param("id") String id,
                       @Param("name") String name,
                       @Param("price") double price,
                       @Param("quantity") int quantity,
                       @Param("description") String description,
                       @Param("categoryId") String categoryId);

    @Query(value = "SELECT COUNT(*) FROM Product WHERE product_id = :id", nativeQuery = true)
    int countById(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM Product WHERE product_id = :id", nativeQuery = true)
    void deleteProductById(@Param("id") String id);

    @Query(value = "SELECT product_id FROM Product ORDER BY CAST(SUBSTRING(product_id, 3) AS UNSIGNED) DESC", nativeQuery = true)
    List<String> findAllIdsDesc();

    @Query(value = "SELECT * FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))", nativeQuery = true)
    List<Product> findByNameContainingIgnoreCase(@Param("keyword") String keyword);

    @Query(value = "SELECT p.* FROM Product p JOIN Category c ON p.category_id = c.category_id WHERE LOWER(c.name) = LOWER(:categoryName)", nativeQuery = true)
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);

    @Query(value = "SELECT * FROM Product p WHERE p.price <= :price", nativeQuery = true)
    List<Product> findByPriceLessThan(@Param("price") double price);

    @Query(value = "SELECT * FROM Product p WHERE p.price BETWEEN :min AND :max", nativeQuery = true)
    List<Product> findByPriceBetween(@Param("min") double min, @Param("max") double max);

    @Modifying
    @Query(value = "UPDATE Product SET quantity = :quantity WHERE product_id = :id", nativeQuery = true)
    void updateProductQuantityNative(@Param("id") String id, @Param("quantity") int quantity);
}