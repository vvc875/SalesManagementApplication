package com.example.sales.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sales.entity.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query(value = "SELECT * FROM Category ", nativeQuery = true)
    List<Category> findAll();

    @Query(value = "SELECT * FROM category WHERE category_id = :id", nativeQuery = true)
    Optional<Category> findById(@Param("id") String id);

    @Query(value = "SELECT category_id FROM category ORDER BY category_id DESC", nativeQuery = true)
    List<String> findAllIdsDesc();

    // Cập nhập danh mục sản phẩm

    // Không  tồn tại danh mục sản phẩm
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM Category WHERE category_id = :id", nativeQuery = true)
    boolean existsById(@Param("id") String id);

    // 🔹 Xoá danh mục theo id
    @Modifying // cho phép query xóa
    @Transactional // nếu lỗi khóa ngoại vi phạm, bị hủy bỏ tránh làm hỏng dữ lệu
    @Query(value = "DELETE FROM Category WHERE category_id = :id", nativeQuery = true)
    void deleteById(@Param("id") String id);

}