package com.example.sales.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;               // Mã nhân viên (tự tăng)

    private String name;           // Họ tên nhân viên
    private String position;       // Chức vụ (VD: Nhân viên bán hàng, Quản lý, v.v.)
    private String phone;          // Số điện thoại
    private String email;          // Email liên hệ
    private Double salary;         // Lương cơ bản
    private LocalDate hireDate;    // Ngày vào làm

    @Column(length = 255)
    private String address;        // Địa chỉ
}
