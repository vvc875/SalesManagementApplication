package com.example.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor // Constructor tiện lợi
public class RevenueByDateDTO {
    private LocalDate date;
    private Double totalRevenue;
}