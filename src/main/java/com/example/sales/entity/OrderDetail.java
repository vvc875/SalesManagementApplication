package com.example.sales.entity;

import jakarta.persistence.*;
import com.example.sales.entity.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_detail")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;      // Số lượng sản phẩm
    private Double price;          // Giá tại thời điểm đặt hàng

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;           // Thuộc đơn hàng nào

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;       // Sản phẩm nào
}
