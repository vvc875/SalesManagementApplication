package com.example.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.example.sales.entity.Category;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;           // Tên sản phẩm
    private Double price;          // Giá
    private Integer quantity;      // Số lượng tồn
    private String description;    // Mô tả

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;    // Danh mục chứa sản phẩm
}
