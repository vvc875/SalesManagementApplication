package com.example.sales.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailRequestDTO {
    private String productId;
    private int quantity;
}