package com.example.sales.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreationDTO {
    private String name;
    private double price;
    private int quantity;
    private String description;
    private String categoryName;
}
