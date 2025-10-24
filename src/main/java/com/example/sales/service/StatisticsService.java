package com.example.sales.service;

import com.example.sales.dto.BestSellingProductDTO;
import com.example.sales.dto.RevenueByDateDTO;
import com.example.sales.dto.TopCustomerDTO;
import com.example.sales.repository.OrderDetailRepository;
import com.example.sales.repository.OrderRepository;
import com.example.sales.repository.ProductRepository;
import com.example.sales.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap; 
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository; 
    
    @Autowired
    private CustomerRepository customerRepository;

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
    public List<BestSellingProductDTO> getBestSellingProducts(int limit, LocalDate date) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results; // <-- Phải là List<Object[]>

        if (date != null) {
            results = orderDetailRepository.findBestSellingProductsByDate(date, pageable);
        } else {
            results = orderDetailRepository.findBestSellingProducts(pageable);
        }

        // --- PHẦN MAPPING QUAN TRỌNG ---
        return results.stream()
                .map(row -> new BestSellingProductDTO(
                        (String) row[0], // productId
                        (String) row[1], // productName
                        // Ép kiểu Number sang Long
                        ((Number) row[2]).longValue() // totalQuantitySold
                ))
                .collect(Collectors.toList());
        // -------------------------------
    }

    // Thống kê khách hàng mua nhiều nhất
    public List<TopCustomerDTO> getTopCustomers(int limit, LocalDate date) {
        PageRequest pageable = PageRequest.of(0, limit);
        if (date != null) {
            // Gọi hàm repository mới (sẽ tạo ở bước 5)
            return orderRepository.findTopCustomerByDate(date, pageable);
        }
        return orderRepository.findTopCustomer(pageable);
    }

    public Map<String, Object> getDashboardData() {
    Map<String, Object> data = new HashMap<>();
    LocalDate today = LocalDate.now();

    // 1. Lấy TỔNG SỐ LƯỢNG sản phẩm bán được hôm nay
    // (Chúng ta sẽ tạo hàm sumTotalQuantitySoldByDate ở Bước 3)
    long totalProductsSoldToday = orderDetailRepository.sumTotalQuantitySoldByDate(today);
    
    // 2. Lấy TỔNG KHÁCH HÀNG (duy nhất) mua hàng hôm nay
    // (Chúng ta sẽ tạo hàm countDistinctCustomersByDate ở Bước 4)
    long totalCustomersToday = orderRepository.countDistinctCustomersByDate(today);
    
    // 3. Lấy TỔNG HÓA ĐƠN hôm nay
    // (Hàm này bạn đã tạo ở bước trước)
    long todayInvoices = orderRepository.countByOrderDate(today);
    
    // 4. Lấy TỔNG DOANH THU hôm nay (Dùng lại hàm cũ)
    double todayRevenue = getDailyRevenue(today, today)
                            .stream()
                            .mapToDouble(RevenueByDateDTO::getTotalRevenue)
                            .sum();

    // Tên các key này ("totalProductsSoldToday", "totalCustomersToday", ...)
    // phải khớp với tệp script.js (Đã sửa lỗi)
    data.put("totalProductsSoldToday", totalProductsSoldToday);
    data.put("totalCustomersToday", totalCustomersToday);
    data.put("todayInvoices", todayInvoices);
    data.put("revenue", todayRevenue); // Đổi tên cho rõ nghĩa
    
    return data;
    }
}