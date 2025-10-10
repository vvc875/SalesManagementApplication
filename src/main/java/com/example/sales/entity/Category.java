package com.example.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;            // Tên danh mục
//    private String description;     // Mô tả danh mục

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Product> products; // Danh sách sản phẩm thuộc danh mục
}
