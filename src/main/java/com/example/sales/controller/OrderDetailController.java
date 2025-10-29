package com.example.sales.controller;

import com.example.sales.dto.OrderDetailRequestDTO;
import com.example.sales.entity.OrderDetail;
import com.example.sales.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/{orderId}/details")
    public List<OrderDetail> getOrderDetailsByOrder(@PathVariable String orderId) {
        return orderDetailService.findByOrderId(orderId);
    }

    @PostMapping("/{orderId}/details")
    public OrderDetail addProductToOrder(
            @PathVariable String orderId,
            @RequestBody OrderDetailRequestDTO detailDTO) {
        return orderDetailService.addProductToOrder(orderId, detailDTO);
    }

    @PutMapping("/details/{detailId}")
    public OrderDetail updateOrderDetail(
            @PathVariable String detailId,
            @RequestBody OrderDetailRequestDTO detailDTO) {
        return orderDetailService.updateOrderDetail(detailId, detailDTO.getQuantity());
    }

    @DeleteMapping("/details/{detailId}")
    public ResponseEntity<Map<String, String>> deleteOrderDetail(@PathVariable String detailId) {
        orderDetailService.deleteOrderDetail(detailId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa thành công chi tiết đơn hàng."));
    }
}