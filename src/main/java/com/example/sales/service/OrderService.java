package com.example.sales.service;

import com.example.sales.dto.OrderCreationDTO;
import com.example.sales.entity.Customer;
import com.example.sales.entity.Employee;
import com.example.sales.entity.Order;
import com.example.sales.entity.OrderDetail;
import com.example.sales.repository.CustomerRepository;
import com.example.sales.repository.EmployeeRepository;
import com.example.sales.repository.OrderDetailRepository;
import com.example.sales.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderDetailRepository orderDetailRepository;
    @Autowired private CustomerRepository customerRepository; // Cần để tìm khách hàng
    @Autowired private EmployeeRepository employeeRepository; // Cần để tìm nhân viên

    // Tự động tạo ID cho đơn hàng mới
    private String generateOrderId() {
        // (Bạn có thể thêm logic tạo ID phức tạp hơn ở đây)
        long count = orderRepository.count();
        return String.format("OR%03d", count + 1);
    }

    //Tạo đơn hàng mới
    @Transactional
    public Order createOrder(OrderCreationDTO orderDTO) {
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + orderDTO.getCustomerId()));

        Employee employee = employeeRepository.findById(orderDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + orderDTO.getEmployeeId()));

        Order newOrder = new Order();
        newOrder.setId(generateOrderId());
        newOrder.setCustomer(customer);
        newOrder.setEmployee(employee);
        newOrder.setOrderDate(LocalDate.now());
        newOrder.setStatus("PENDING"); // Trạng thái ban đầu
        newOrder.setTotalAmount(0.0); // Tổng tiền ban đầu là 0

        return orderRepository.save(newOrder);
    }

    //Tự động cập nhật tổng tiền (được gọi bởi OrderDetailService)
    @Transactional
    public void updateOrderTotalAmount(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng để cập nhật tổng tiền"));

        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
        double total = details.stream()
                .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                .sum();

        order.setTotalAmount(total);
        orderRepository.save(order);
    }

    // Xem danh sách đơn hàng
    public List<Order> getAllOrder(){
        return orderRepository.findAll();
    }

    // Xem chi tiết một đơn hàng
    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
    }

    // xoá một đơn hàng
    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy đơn hàng để xóa!");
        }
        orderRepository.deleteById(id);
    }
}