package com.example.sales.repository;

import com.example.sales.dto.BestSellingProductDTO;
import com.example.sales.entity.OrderDetail;
import org.springframework.data.domain.Pageable; // Xóa
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate; // Xóa
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    @Query(value = "SELECT * FROM order_detail od WHERE od.orderdetail_id = :id", nativeQuery = true)
    Optional<OrderDetail> getOrderDetailById(@Param("id") String id);

    @Query(value = "SELECT * FROM order_detail od WHERE od.order_id = :orderId AND od.product_id = :productId", nativeQuery = true)
    Optional<OrderDetail> findByOrderIdAndProductId(@Param("orderId") String orderId, @Param("productId") String productId);

    @Modifying
    @Query(value = "DELETE FROM order_detail WHERE orderdetail_id = :id", nativeQuery = true)
    void deleteOrderDetailById(@Param("id") String id);

    @Query(value = "SELECT * FROM order_detail od WHERE od.order_id = :orderId", nativeQuery = true)
    List<OrderDetail> findByOrderId(@Param("orderId") String orderId);

    @Query(value = "SELECT SUM(price * quantity) FROM order_detail WHERE order_id = :orderId", nativeQuery = true)
    Double calculateTotalAmount(@Param("orderId") String orderId);

    @Query(value = """
            SELECT p.product_id AS productId, p.name AS productName,
                   CAST(SUM(od.quantity) AS SIGNED) AS totalSold 
            FROM order_detail od
            JOIN product p ON od.product_id = p.product_id
            GROUP BY p.product_id, p.name
            ORDER BY totalSold DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<BestSellingProductDTO> findBestSellingProducts(@Param("limit") int limit);

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
    long countAllDetails();
}