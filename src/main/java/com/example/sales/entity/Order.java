package com.example.sales.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate orderDate;   // Ngày đặt hàng
    private Double totalAmount;    // Tổng tiền đơn hàng

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;     // Khách hàng đặt đơn

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;     // Nhân viên phụ trách

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails; // Chi tiết đơn hàng
}
