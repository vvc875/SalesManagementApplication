package com.example.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BestSellingProductDTO {
    private String productId;
    private String productName;
    private Long totalQuantitySold;
}