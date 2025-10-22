package com.example.sales.repository;

import com.example.sales.dto.BestSellingProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sales.entity.OrderDetail;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

//    // ID tu dong
//    @Query(value = "SELECT MAX(CAST(SUBSTRING(order_detail_id, 3) AS UNSIGNED)) FROM order_detail ", nativeQuery = true)
//    long count(String orderId);

    //Thêm sản phảm vào đơn hàng
    @Query(value = "SELECT * FROM order_detail od WHERE od.order_id = :orderId AND od.product_id = :productId", nativeQuery = true)
    Optional<OrderDetail> findByOrderIdAndProductId(@Param("orderId") String orderId, @Param("productId") String productId);

    //Cập nhật số lượng sản phẩm
    @Query(value = "SELECT * FROM order_detail od WHERE od.orderdetail_id = :detailId", nativeQuery = true)
    Optional<OrderDetail> findById(@Param("detailId") String detailId);

    //Xoá sản phẩm khỏi đơn hàng
    @Modifying
    @Query(value = "DELETE FROM order_detail WHERE orderdetail_id = :detailId", nativeQuery = true)
    void deleteOrderDetailById(@Param("detailId") String detailId);

    @Query(value = "SELECT * FROM order_detail od WHERE od.order_id = :orderId", nativeQuery = true)
    List<OrderDetail> findByOrderId(@Param("orderId") String orderId);

    //Tong so tien don hang
    @Query(value = "SELECT SUM(price * quantity) FROM order_detail WHERE order_id = :orderId", nativeQuery = true)
    Double calculateTotalAmount(@Param("orderId") String orderId);

    //Thong ke doanh thu theo ngay
    @Query(value = "SELECT order_date, SUM(total_amount) AS total " +
            "FROM orders " +
            "WHERE order_date BETWEEN :startDate AND :endDate " +
            "GROUP BY order_date " +
            "ORDER BY order_date ASC", nativeQuery = true)
    List<Object[]> findDailyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //Top San Pham ban chay
    @Query(value = "SELECT p.product_id AS productId, p.name AS productName, SUM(od.quantity) AS totalSold " +
            "FROM order_detail od " +
            "JOIN product p ON od.product_id = p.product_id " +
            "GROUP BY p.product_id, p.name " +
            "ORDER BY totalSold DESC ", nativeQuery = true)
    List<BestSellingProductDTO> findBestSellingProducts(Pageable pageable);

    @Modifying
    @Query(value = """
        INSERT INTO Order_Detail (orderdetail_id, order_id, product_id, quantity, price) 
        VALUES (:id, :orderId, :productId, :quantity, :price)
        """, nativeQuery = true)
    void insertOrderDetailNative(@Param("id") String id,
                                 @Param("orderId") String orderId,
                                 @Param("productId") String productId,
                                 @Param("quantity") int quantity,
                                 @Param("price") double price);

    @Modifying
    @Query(value = "UPDATE Order_Detail SET quantity = :quantity WHERE orderdetail_id = :id", nativeQuery = true)
    void updateOrderDetailQuantityNative(@Param("id") String id, @Param("quantity") int quantity);

    @Modifying
    @Query(value = "UPDATE Order_Detail SET quantity = :quantity, price = :price WHERE orderdetail_id = :id", nativeQuery = true)
    void updateOrderDetailQuantityAndPriceNative(@Param("id") String id, @Param("quantity") int quantity, @Param("price") double price);

    @Query(value = "SELECT COUNT(*) FROM Order_Detail", nativeQuery = true)
    long count();
}