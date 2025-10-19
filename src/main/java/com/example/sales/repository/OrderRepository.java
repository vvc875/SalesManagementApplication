package com.example.sales.repository;

import com.example.sales.dto.TopCustomerDTO;
import com.example.sales.entity.Customer;
import com.example.sales.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query(value = "SELECT MAX(CAST(SUBSTRING(order_id, 3) AS UNSIGNED)) FROM orders", nativeQuery = true)
    long count();

    //Tạo đơn hàng mới


    //Lịch sử khách hàng bằng ID
    @Query(value = "SELECT * FORM orders WHERE customer_id = :customerId ORDER BY order_date DESC ", nativeQuery = true)
    List<Order> findOrdersByCustomerId(@Param("customerId") String customerId);
    // Them san pham vao don hang

    //Tự động cập nhật tổng tiền (được gọi bởi OrderDetailService)
    // tim bang id
    @Query(value = "SELECT * FROM orders WHERE order_id = :orderId", nativeQuery = true)
    Optional<Order> findById(@Param("orderId") String orderId);

    // Lấy tất cả đơn hàng
    @Query(value = "SELECT * FROM orders", nativeQuery = true)
    List<Order> findAll();

    // Kiểm tra tồn tại đơn hàng
    @Query(value = "SELECT COUNT(*) > 0 FROM orders WHERE order_id = :orderId", nativeQuery = true)
    boolean existsById(@Param("orderId") String orderId);

    // Xóa đơn hàng theo ID
    @Modifying
    @Query(value = "DELETE FROM orders WHERE order_id = :orderId", nativeQuery = true)
    void deleteById(@Param("orderId") String orderId);

    // thong ke doanh thu theo ngay
    @Query(value = "SELECT orderDate, SUM(total_amount) AS total " +
            "FROM orders " +
            "WHERE orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY orderDate ", nativeQuery = true)
    List<Object[]> findDailyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Doanh thu theo tháng trong năm
    @Query(value = "SELECT MONTH(orderDate) AS month, SUM(total_amount) AS total " +
            "FROM orders " +
            "WHERE YEAR(orderDate) = :year " +
            "GROUP BY MONTH(orderDate) " +
            "ORDER BY month ASC", nativeQuery = true)
    List<Object[]> findMonthlyRevenue(@Param("year") int year);

    // Doanh thu theo nhân viên
    @Query(value = "SELECT e.employee_id, e.name, SUM(o.total_amount) AS total " +
            "FROM orders o JOIN employee e ON o.employee_id = e.employee_id " +
            "GROUP BY e.employee_id, e.name " +
            "ORDER BY total DESC", nativeQuery = true)
    List<Object[]> findRevenueByEmployee();

    // Top khách hàng mua nhiều nhất
    @Query(value = "SELECT c.customer_id, c.name, SUM(o.total_amount) AS total " +
            "FROM orders o JOIN customer c ON o.customer_id = c.customer_id " +
            "GROUP BY c.customer_id, c.name " +
            "ORDER BY total DESC ", nativeQuery = true)
    List<TopCustomerDTO> findTopCustomers(Pageable pageable);

}