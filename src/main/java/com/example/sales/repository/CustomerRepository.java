package com.example.sales.repository;

import com.example.sales.entity.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sales.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    // lay danh sach tu ID DESC lon - nho
    @Query(value = "SELECT customer_id FROM customer ORDER BY customer_id DESC", nativeQuery = true)
    List<String> findAllIdsDesc();

    // Lay tat ca danh sach khach hang
    @Query(value = "SELECT c FROM customer c", nativeQuery = true)
    List<Customer> getAllCustomers();

    // Tìm kiếm khách hàng theo customerId
    @Query(value = "SELECT c FROM customer c WHERE c.id = :id", nativeQuery = true)
    Customer getCustomerById(@Param("id") String id);

    //Thêm một khách hàng mới

    //Cập nhật thông tin của khách hàng theo customerId

    //Xoá khách hoàng theo customerId
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM customer WHERE customer_id = :id", nativeQuery = true)
    void deleteCustomerById(@Param("id") String id);

    //Tìm kiếm khách hàng theo tên hoặc email
    @Query(value = "SELECT * FROM customer " +
            "WHERE LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(email) LIKE LOWER(CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    List<Customer> searchByNameOrEmail(@Param("keyword") String keyword);

    //Tổng số khách hàng
    @Query(value = "SELECT COUNT(DISTINCT customer_id) FROM customer", nativeQuery = true)
    long countTotalCustomers();

    // Tao don hang moi
    @Query(value = "SELECT * FROM customer c WHERE c.customer_id = :customerId", nativeQuery = true)
    Optional<Customer> findById(@Param("customerId") String customerId);

}