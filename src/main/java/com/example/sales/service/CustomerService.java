package com.example.sales.service;

import com.example.sales.entity.Customer;
import com.example.sales.entity.Order;
import com.example.sales.repository.CustomerRepository;
import com.example.sales.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository; // Giữ lại nếu getOrderHistory cần dùng

    // Lấy tất cả danh sách khách hàng
    public List<Customer> getAllCustomer() {
        return customerRepository.getAllCustomer(); // Gọi hàm native đã định nghĩa
    }

    // Tìm kiếm khách hàng theo customerId
    public Customer getCustomerById(String id) {
        // Gọi hàm native findByIdNative và xử lý Optional
        return customerRepository.getCustomerById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng!"));
    }

    // Sinh customerId
    private String generateCustomerId() {
        List<String> ids = customerRepository.findAllIdsDesc();
        if (ids.isEmpty()) return "C001";

        String lastId = ids.get(0);
        int num = Integer.parseInt(lastId.replace("C", ""));
        return String.format("C%03d", num + 1);
    }

    // Thêm một khách hàng mới
    @Transactional
    public Customer addCustomer(Customer customer) {
        String newId = generateCustomerId();
        customer.setId(newId); // Gán ID mới

        customerRepository.addCustomer(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getEmail()
        );

        return getCustomerById(newId);
    }

    // Cập nhật thông tin của khách hàng theo customerId
    @Transactional
    public Customer updateCustomer(String id, Customer customerDetails) {

        if (customerRepository.countById(id) == 0) {
            throw new RuntimeException("Không tìm thấy khách hàng để cập nhật!");
        }


        customerRepository.updateCustomer(
                id,
                customerDetails.getName(),
                customerDetails.getPhone(),
                customerDetails.getAddress(),
                customerDetails.getEmail()
        );

        return getCustomerById(id);
    }

    // Xoá khách hàng theo customerId
    @Transactional
    public void deleteCustomer(String id) {

        if (customerRepository.countById(id) == 0) {
            throw new RuntimeException("Không tìm thấy khách hàng để xóa!");
        }

        customerRepository.deleteCustomerById(id);
    }

    // Tìm kiếm khách hàng theo tên hoặc email
    public List<Customer> searchCustomers(String keyword) {
        return customerRepository.searchByNameOrEmail(keyword);
    }

    //Tìm kiếm khách hàng theo số điện thoại
    public List<Customer> searchCustomerByPhone(String phone){
        return customerRepository.searchByPhone(phone);
    }


    // Tổng số khách hàng
    public long getTotalCustomers() {
        return customerRepository.countTotalCustomers();
    }

    // Lịch sử đơn hàng của khách hàng
    public List<Order> getOrderHistory(String customerId) {
        getCustomerById(customerId);
        return orderRepository.findOrderByCustomerId(customerId);
    }
}