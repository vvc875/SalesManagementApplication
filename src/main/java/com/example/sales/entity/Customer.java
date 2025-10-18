package com.example.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "customer")

public class Customer{

    @Id
    @Column(name="customer_id")
    private String id;

    private String name;
    private String phone;
    private String address;
    private String email;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Order> orders;
}