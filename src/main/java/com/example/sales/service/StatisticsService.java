package com.example.sales.service;

import com.example.sales.dto.BestSellingProductDTO;
import com.example.sales.dto.RevenueByDateDTO;
import com.example.sales.dto.TopCustomerDTO;
import com.example.sales.repository.OrderDetailRepository;
import com.example.sales.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // Thống kê doanh thu theo ngày
    public List<RevenueByDateDTO> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        return orderRepository.findDailyRevenue(startDate, endDate).stream()
                .map(result -> new RevenueByDateDTO((java.sql.Date.valueOf(result[0].toString())).toLocalDate(), (Double) result[1]))
                .collect(Collectors.toList());
    }

    // Thống kê doanh thu theo tháng
    public List<Object[]> getMonthlyRevenue(int year) {

        return orderRepository.findMonthlyRevenue(year);
    }

    // Thống kê doanh thu theo nhân viên
    public List<Object[]> getRevenueByEmployee() {
        return orderRepository.findRevenueByEmployee();
    }

    // Thống kê sản phẩm bán chạy
    public List<BestSellingProductDTO> getBestSellingProducts(int limit) {
        return orderDetailRepository.findBestSellingProducts(limit);
    }

    // Thống kê khách hàng mua nhiều nhất
    public List<TopCustomerDTO> getTopCustomers(int limit) {
        return orderRepository.findTopCustomer(limit);
    }
}
