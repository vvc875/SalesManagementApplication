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
    @Column(name="employee_id")
    private String id;

    private String name;
    private String position;
    private String phone;
    private String email;
    private Double salary;
    private LocalDate hireDate;

    @Column(length = 255)
    private String address;
}
