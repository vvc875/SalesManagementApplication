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
@RequestMapping("/orders") // Tất cả thao tác đều thông qua /orders
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    // Lấy tất cả chi tiết của một đơn hàng
    @GetMapping("/{orderId}/details")
    public List<OrderDetail> getOrderDetailsByOrder(@PathVariable String orderId) {
        return orderDetailService.findByOrderId(orderId);
    }

    // Thêm sản phẩm vào đơn hàng
    @PostMapping("/{orderId}/details")
    public OrderDetail addProductToOrder(
            @PathVariable String orderId,
            @RequestBody OrderDetailRequestDTO detailDTO) {
        return orderDetailService.addProductToOrder(orderId, detailDTO);
    }

    // Cập nhật số lượng sản phẩm
    @PutMapping("/details/{detailId}")
    public OrderDetail updateOrderDetail(
            @PathVariable String detailId,
            @RequestBody OrderDetailRequestDTO detailDTO) {
        return orderDetailService.updateOrderDetail(detailId, detailDTO.getQuantity());
    }

    // Xóa một sản phẩm khỏi đơn hàng
    @DeleteMapping("/details/{detailId}")
    public ResponseEntity<Map<String, String>> deleteOrderDetail(@PathVariable String detailId) {
        orderDetailService.deleteOrderDetail(detailId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa thành công chi tiết đơn hàng."));
    }
}