package com.example.sales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sales.entity.OrderDetail;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}