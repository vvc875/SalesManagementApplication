package com.example.sales.repository;

import com.example.sales.dto.BestSellingProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sales.entity.OrderDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    @Query("SELECT od FROM OrderDetail od WHERE od.order.id = :orderId")
    List<OrderDetail> findByOrderId(String orderId);

    @Query("SELECT od FROM OrderDetail od WHERE od.order.id = :orderId")
    List<OrderDetail> findByOrder_Id(String orderId);

    Optional<OrderDetail> findByOrderIdAndProductId(String orderId, String productId);

    @Query("SELECT new com.example.sales.dto.BestSellingProductDTO(p.id, p.name, SUM(od.quantity)) " +
            "FROM OrderDetail od JOIN od.product p " +
            "JOIN od.order o WHERE o.status = 'COMPLETED' " +
            "GROUP BY p.id, p.name " +
            "ORDER BY SUM(od.quantity) DESC")
    List<BestSellingProductDTO> findBestSellingProducts(Pageable pageable);
}