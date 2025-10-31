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

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderDetailRepository orderDetailRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private EmployeeRepository employeeRepository;

    // Lấy tất cả đơn hàng hiện
    public List<Order> getAllOrders(LocalDate date) {
        if (date != null) {
            // Gọi đúng tên hàm Repository mới
            return orderRepository.getOrderByDate(date);
        } else {
            // Gọi đúng tên hàm Repository mới
            return orderRepository.findAllOrdersWithDetails();
        }
    }

    // Tìm đơn hàng theo ngày
    public List<Order> getOrdersByDate(LocalDate date) {
        return orderRepository.getOrderByDate(date);
    }

    // Tìm chi tiết đơn hàng
    public Order getOrderByIdWithDetails(String orderId) {

        return orderRepository.findOrderByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
    }

    // Lấy đơn hàng theo orderId
    public Order getOrderById(String orderId) {
        return orderRepository.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
    }

    // Sinh orderId
    private String generateOrderId() {
        List<String> ids = orderRepository.findAllIdsDesc();
        if (ids.isEmpty()) {
            return "OR001";
        }

        String lastId = ids.get(0);
        int num = Integer.parseInt(lastId.substring(2));
        return String.format("OR%03d", num + 1);
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
        String initialStatus = "Pending";
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

        // Fetch lại để trả về entity
        return getOrderByIdWithDetails(newOrderId);
    }

    // Cập nhật tổng tiền của đơn hàng
    @Transactional
    public void updateOrderTotalAmount(String orderId) {
        // Kiểm tra đơn hàng tồn tại
        Order order = getOrderByIdWithDetails(orderId);

        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
        double total = details.stream()
                .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                .sum();

        // Gọi hàm UPDATE native chỉ cho totalAmount
        orderRepository.updateOrderTotalAmount(orderId, total);
    }

    // Xoá một đơn hàng
    @Transactional
    public void deleteOrder(String id) {
        if (orderRepository.countById(id) == 0) {
            throw new RuntimeException("Không tìm thấy đơn hàng để xóa!");
        }
        // Trước khi xóa Order, cần xóa các OrderDetail liên quan (nếu không có ON DELETE CASCADE)
        List<OrderDetail> details = orderDetailRepository.findByOrderId(id);
        for (OrderDetail detail : details) {
            orderDetailRepository.deleteOrderDetailById(detail.getId()); // Xóa từng detail
        }
        // Sau đó mới xóa Order
        orderRepository.deleteOrderById(id); // Gọi hàm native deleteById
    }

    // Hàm cập nhật trạng thái
    @Transactional
    public Order updateOrderStatus(String orderId, String newStatus) {
        if (orderRepository.countById(orderId) == 0) {
            throw new RuntimeException("Không tìm thấy đơn hàng để cập nhật trạng thái!");
        }
        String formattedStatus = newStatus;
        if(formattedStatus != null && formattedStatus.length() > 1 && formattedStatus.startsWith("\"") && formattedStatus.endsWith("\"")) {
            formattedStatus = formattedStatus.substring(1, formattedStatus.length() - 1);
        }

        if(formattedStatus != null && formattedStatus.isEmpty()) {
            formattedStatus = newStatus.substring(0, 1).toUpperCase() + newStatus.substring(1).toLowerCase();
        }
        orderRepository.updateOrderStatus(orderId, formattedStatus);
        return getOrderByIdWithDetails(orderId);
    }

    public List<Order> findOrdersByDate(LocalDate date) {
        return orderRepository.findOrderByDate(date);
    }
}