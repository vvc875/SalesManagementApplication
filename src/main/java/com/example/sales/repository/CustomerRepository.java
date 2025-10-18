package com.example.sales.repository;

import com.example.sales.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sales.entity.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    @Query("SELECT c FROM Customer c")
    List<Customer> getAllCustomers();

    @Query("SELECT c FROM Customer c WHERE c.id = :id")
    Customer getCustomerById(@Param("id") String id);

    @Query("DELETE FROM Customer c WHERE c.id = :id")
    void deleteCustomerById(@Param("id") String id);

    @Query("""
           SELECT c FROM Customer c
           WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
           """)
    List<Customer> searchByNameOrEmail(@Param("keyword") String keyword);

    @Query("SELECT COUNT(c) FROM Customer c")
    long countTotalCustomers();

    @Query("SELECT c.id FROM Customer c ORDER BY c.id DESC")
    List<String> findAllIdsDesc();
}