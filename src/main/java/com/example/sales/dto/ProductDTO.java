package com.example.sales.dto;

import com.example.sales.entity.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


public class ProductDTO {
    private Long id;
    private String name;
    private Double price;          // Giá
    private Integer quantity;      // Số lượng tồn
    private String description;
    private Category category;

    public ProductDTO(Long id, String name, Double price, Integer quantity, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
    }
}
