package com.example.sales.repository;

import com.example.sales.dto.TopCustomerDTO; // Giữ lại DTO nếu dùng native query trả Object[]
import com.example.sales.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal; // Vẫn cần import nếu dùng
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query(value = "SELECT * FROM Orders WHERE order_id = :id", nativeQuery = true)
    Optional<Order> getOrderById(@Param("id") String id);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer c LEFT JOIN FETCH o.employee e WHERE o.id = :id")
    Optional<Order> findOrderByIdWithDetails(@Param("id") String id);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer c LEFT JOIN FETCH o.employee e ORDER BY o.id ASC")
    List<Order> findAllOrdersWithDetails();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer c LEFT JOIN FETCH o.employee e WHERE o.orderDate = :date ORDER BY o.orderDate DESC")
    List<Order> findOrdersByDateWithDetails(@Param("date") LocalDate date);

    @Query(value = "SELECT COUNT(*) FROM Orders WHERE order_id = :id", nativeQuery = true)
    int countById(@Param("id") String id); // Đổi tên

    @Modifying
    @Query(value = "DELETE FROM Orders WHERE order_id = :id", nativeQuery = true)
    void deleteOrderById(@Param("id") String id);

    @Modifying
    @Query(value = """
        INSERT INTO Orders (order_id, customer_id, employee_id, order_date, status, total_amount) 
        VALUES (:id, :customerId, :employeeId, :orderDate, :status, :totalAmount)
        """, nativeQuery = true)
    void insertOrder(@Param("id") String id,
                     @Param("customerId") String customerId,
                     @Param("employeeId") String employeeId,
                     @Param("orderDate") LocalDate orderDate,
                     @Param("status") String status,
                     @Param("totalAmount") double totalAmount);

    @Modifying
    @Query(value = "UPDATE Orders SET total_amount = :totalAmount WHERE order_id = :id", nativeQuery = true)
    void updateOrderTotalAmount(@Param("id") String id, @Param("totalAmount") double totalAmount);

    @Modifying
    @Query(value = "UPDATE Orders SET status = :status WHERE order_id = :id", nativeQuery = true)
    void updateOrderStatusNative(@Param("id") String id, @Param("status") String status);

    // Lịch sử đơn hàng của khách hàng
    @Query(value = "SELECT * FROM Orders WHERE customer_id = :customerId ORDER BY order_date DESC", nativeQuery = true)
    List<Order> findOrderByCustomerId(@Param("customerId") String customerId);

    // Thống kê doanh thu theo ngày
    @Query(value = "SELECT DATE(o.order_date), SUM(o.total_amount) FROM Orders o WHERE o.status = 'COMPLETED' AND DATE(o.order_date) BETWEEN :startDate AND :endDate GROUP BY DATE(o.order_date)", nativeQuery = true)
    List<Object[]> findDailyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Doanh thu theo tháng trong năm (ĐÃ SỬA)
    @Query(value = """
            SELECT MONTH(o.order_date) AS month, SUM(o.total_amount) AS total
            FROM Orders o
            WHERE YEAR(o.order_date) = :year AND o.status = 'COMPLETED'
            GROUP BY MONTH(o.order_date)
            ORDER BY month ASC
            """, nativeQuery = true)
    List<Object[]> findMonthlyRevenue(@Param("year") int year);

    // Doanh thu theo nhân viên (ĐÃ SỬA)
    @Query(value = """
            SELECT e.employee_id, e.name, SUM(o.total_amount) AS total
            FROM Orders o JOIN Employee e ON o.employee_id = e.employee_id
            WHERE o.status = 'COMPLETED'
            GROUP BY e.employee_id, e.name
            ORDER BY total DESC
            """, nativeQuery = true)
    List<Object[]> findRevenueByEmployee();

    // Top khách hàng mua nhiều nhất (ĐÃ SỬA)
    @Query(value = """
            SELECT c.customer_id, c.name, SUM(o.total_amount) AS total
            FROM Orders o JOIN Customer c ON o.customer_id = c.customer_id
            WHERE o.status = 'COMPLETED'
            GROUP BY c.customer_id, c.name
            ORDER BY total DESC
            """, nativeQuery = true)
    List<TopCustomerDTO> findTopCustomer(Pageable pageable);

    @Query(value = """
            SELECT c.customer_id, c.name, SUM(o.total_amount) AS total
            FROM Orders o JOIN Customer c ON o.customer_id = c.customer_id
            WHERE o.status = 'COMPLETED'
            AND o.order_date = :date  
            GROUP BY c.customer_id, c.name
            ORDER BY total DESC
            """, nativeQuery = true)
    List<TopCustomerDTO> findTopCustomerByDate(@Param("date") LocalDate date, Pageable pageable);
    
    @Query(value = "SELECT COUNT(DISTINCT o.customer_id) " +
        "FROM Orders o " +
        "WHERE o.order_date = :date AND o.status = 'COMPLETED'", nativeQuery = true)
    long countDistinctCustomersByDate(@Param("date") LocalDate date);

    @Query(value = "SELECT COUNT(o.order_id) FROM Orders o WHERE o.order_date = :date", nativeQuery = true)
    long countByOrderDate(@Param("date") LocalDate date);

    @Query(value = "SELECT order_id FROM Orders ORDER BY CAST(SUBSTRING(order_id, 3) AS UNSIGNED) DESC", nativeQuery = true)
    List<String> findAllIdsDesc();
}