package com.example.sales.repository;

import com.example.sales.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// Vẫn cần extends JpaRepository để Spring quản lý Bean
public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Query(value = "SELECT * FROM Customer", nativeQuery = true)
    List<Customer> getAllCustomer();

    @Query(value = "SELECT * FROM Customer WHERE customer_id = :id", nativeQuery = true)
    Optional<Customer> getCustomerById(@Param("id") String id);

    @Query(value = "SELECT COUNT(*) FROM Customer WHERE customer_id = :id", nativeQuery = true)
    int countById(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM Customer WHERE customer_id = :id", nativeQuery = true)
    void deleteCustomerById(@Param("id") String id); // Giữ tên này vì Service đang gọi


    @Modifying
    @Query(value = "INSERT INTO Customer (customer_id, name, phone, address, email) " +
            "VALUES (:id, :name, :phone, :address, :email)", nativeQuery = true)
    void addCustomer(@Param("id") String id,
                              @Param("name") String name,
                              @Param("phone") String phone,
                              @Param("address") String address,
                              @Param("email") String email);

    @Modifying
    @Query(value = "UPDATE Customer SET name = :name, phone = :phone, address = :address, email = :email " +
            "WHERE customer_id = :id", nativeQuery = true)
    void updateCustomer(@Param("id") String id,
                              @Param("name") String name,
                              @Param("phone") String phone,
                              @Param("address") String address,
                              @Param("email") String email);


    @Query(value = "SELECT customer_id FROM Customer ORDER BY CAST(SUBSTRING(customer_id, 2) AS UNSIGNED) DESC", nativeQuery = true)
    List<String> findAllIdsDesc();

    @Query(value = "SELECT * FROM Customer " +
            "WHERE LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(email) LIKE LOWER(CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    List<Customer> searchByNameOrEmail(@Param("keyword") String keyword);

    @Query(value="SELECT * FROM Customer WHERE phone = :phone", nativeQuery = true)
    List<Customer> searchByPhone(@Param("phone") String phone);

    @Query(value = "SELECT COUNT(*) FROM Customer", nativeQuery = true)
    long countTotalCustomers();

}