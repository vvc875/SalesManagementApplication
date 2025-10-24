package com.example.sales.controller;

import com.example.sales.dto.OrderCreationDTO;
import com.example.sales.entity.Order;
import com.example.sales.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/orders") // Đổi thành "/orders" cho chuẩn RESTful
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            LocalDate date 
    ) {
        return orderService.getAllOrders(date); 
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable String orderId) {

        return orderService.getOrderById(orderId);
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderCreationDTO orderDTO) {
        return orderService.createOrder(orderDTO);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Đã xóa thành công đơn hàng " + orderId);
    }
}