package com.example.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopCustomerDTO {
    private String customerId;
    private String customerName;
    private Double totalAmountSpent;
}