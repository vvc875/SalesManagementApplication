package com.example.sales.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreationDTO {
    private String customerId;
    private String employeeId;
}