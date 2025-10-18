package com.example.sales.repository;

import com.example.sales.dto.TopCustomerDTO;
import com.example.sales.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime; // Sửa từ LocalDate thành LocalDateTime
import java.util.List;
import java.util.Optional; // Import Optional

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT o FROM Order o WHERE o.status = :status")
    List<Order> findByStatus(@Param("status") String status);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.orderDate DESC")
    List<Order> findOrdersByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT COUNT(o) FROM Order o")
    long countTotalOrders();

    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    Optional<BigDecimal> sumTotalAmount();

    @Query("SELECT FUNCTION('DATE', o.orderDate), SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED' AND FUNCTION('DATE', o.orderDate) BETWEEN :startDate AND :endDate GROUP BY FUNCTION('DATE', o.orderDate)")
    List<Object[]> findDailyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate), SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED' AND FUNCTION('YEAR', o.orderDate) = :year GROUP BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate)")
    List<Object[]> findMonthlyRevenue(@Param("year") int year);

    @Query("SELECT e.name, SUM(o.totalAmount) FROM Order o JOIN o.employee e WHERE o.status = 'COMPLETED' GROUP BY e.id, e.name ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> findRevenueByEmployee();

    @Query("SELECT new com.example.sales.dto.TopCustomerDTO(c.id, c.name, SUM(o.totalAmount)) " +
            "FROM Order o JOIN o.customer c " +
            "WHERE o.status = 'COMPLETED' " +
            "GROUP BY c.id, c.name " +
            "ORDER BY SUM(o.totalAmount) DESC")
    List<TopCustomerDTO> findTopCustomers(Pageable pageable);
}