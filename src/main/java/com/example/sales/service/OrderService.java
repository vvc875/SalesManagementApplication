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
            return orderRepository.findOrdersByDateWithDetails(date);
        } else {
            // Gọi đúng tên hàm Repository mới
            return orderRepository.findAllOrdersWithDetails();
        }
    }

    public List<Order> getOrdersByDate(LocalDate date) {
        // Gọi đúng tên hàm Repository
        return orderRepository.findOrdersByDateWithDetails(date);
    }

    public Order getOrderByIdWithDetails(String orderId) { // Đổi tên hàm Service cho rõ
        // Gọi đúng tên hàm Repository
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
            return "OR001"; // 2. Nếu không có đơn hàng nào, bắt đầu từ 1
        }

        String lastId = ids.get(0); // Lấy ID lớn nhất (ví dụ: "OR153")
        // Tách lấy số (bỏ 2 ký tự "OR")
        int num = Integer.parseInt(lastId.substring(2)); 
        // Cộng 1 và format lại (ví dụ: "OR154")
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
        orderRepository.updateOrderStatus(orderId, newStatus.toUpperCase());
        return getOrderByIdWithDetails(orderId);
    }

    public List<Order> findOrdersByDate(LocalDate date) {
        return orderRepository.findOrderByDate(date);
    }
}