package com.example.sales.controller;

import com.example.sales.dto.BestSellingProductDTO;
import com.example.sales.dto.RevenueByDateDTO;
import com.example.sales.dto.TopCustomerDTO;
import com.example.sales.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/revenue/daily")
    public List<RevenueByDateDTO> getDailyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return statisticsService.getDailyRevenue(startDate, endDate);
    }

    @GetMapping("/revenue/monthly")
    public List<Object[]> getMonthlyRevenue(@RequestParam int year) {
        return statisticsService.getMonthlyRevenue(year);
    }

    @GetMapping("/revenue/by-employee")
    public List<Object[]> getRevenueByEmployee() {
        return statisticsService.getRevenueByEmployee();
    }

    @GetMapping("/products/best-selling")
    public List<BestSellingProductDTO> getBestSellingProducts(@RequestParam(defaultValue = "10") int limit) {
        return statisticsService.getBestSellingProducts(limit);
    }

    @GetMapping("/customers/top")
    public List<TopCustomerDTO> getTopCustomers(@RequestParam(defaultValue = "10") int limit) {
        return statisticsService.getTopCustomers(limit);
    }
}