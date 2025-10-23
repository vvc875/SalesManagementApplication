package com.example.sales.service;

import com.example.sales.dto.OrderCreationDTO;
import com.example.sales.entity.*;
import com.example.sales.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderDetailRepository orderDetailRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ProductRepository productRepository;

    // Lấy tất cả đơn hàng hiện
    public List<Order> getAllOrder(){
        return orderRepository.getAllOrder();
    }

    // Lấy đơn hàng theo orderId
    public Order getOrderById(String orderId) {
        return orderRepository.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
    }

    // Sinh orderId
    private String generateOrderId() {
        long count = orderRepository.countTotalOrder();
        return String.format("OR%03d", count + 1);
    }

    // Tạo đơn hàng mới
    @Transactional
    public Order createOrder(OrderCreationDTO orderDTO) {
        Customer customer = customerRepository.getCustomerById(orderDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + orderDTO.getCustomerId()));

        Employee employee = employeeRepository.getEmployeeById(orderDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + orderDTO.getEmployeeId()));

        String newOrderId = generateOrderId();
        LocalDate now = LocalDate.now();
        String initialStatus = "PENDING";
        double initialTotal = 0.0;

        // Gọi hàm INSERT native
        orderRepository.insertOrder(
                newOrderId,
                customer.getId(),
                employee.getId(),
                now,
                initialStatus,
                initialTotal
        );

        return getOrderById(newOrderId);
    }

    // Cập nhật tổng tiền của đơn hàng
    @Transactional
    public void updateOrderTotalAmount(String orderId) {
        Order order = getOrderById(orderId);

        Double total = orderDetailRepository.calculateTotalAmount(orderId);

        orderRepository.updateOrderTotalAmount(orderId, (total != null) ? total : 0.0);
    }

    // Xoá một đơn hàng
    @Transactional
    public void deleteOrder(String id) {
        if (orderRepository.countById(id) == 0) {
            throw new RuntimeException("Không tìm thấy đơn hàng để xóa!");
        }

        List<OrderDetail> details = orderDetailRepository.findByOrderId(id);
        for (OrderDetail detail : details) {

            Product product = productRepository.getProductById(detail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sản phẩm " + detail.getProduct().getId()));

            // Hoàn trả lại số lượng vào kho
            int quantityToReturn = detail.getQuantity();
            int newProductQuantity = product.getQuantity() + quantityToReturn;
            productRepository.updateProductQuantityNative(product.getId(), newProductQuantity);

            // Xóa detail
            orderDetailRepository.deleteOrderDetailById(detail.getId());
        }

        // Sau đó mới xóa Order
        orderRepository.deleteOrderById(id);
    }
}