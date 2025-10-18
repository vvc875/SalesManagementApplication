package com.example.sales.service;

import com.example.sales.entity.Customer;
import com.example.sales.entity.Order;
import com.example.sales.repository.CustomerRepository;
import com.example.sales.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    private String generateCustomerId() {
        List<String> ids = customerRepository.findAllIdsDesc();
        if (ids.isEmpty()) return "C001";

        String lastId = ids.get(0); // Lấy ID lớn nhất hiện tại
        int num = Integer.parseInt(lastId.replace("C", ""));
        return String.format("C%03d", num + 1);
    }

    //Lấy tất cả danh sách khách hàng
    public List<Customer> getAllCustomer() {
        return customerRepository.getAllCustomers();
    }

    //Tìm kiếm khách hàng theo customerId
    public Customer getCustomerById(String id) {
        return customerRepository.getCustomerById(id);
    }

    //Thêm một khách hàng mới
    public Customer addCustomer(Customer customer) {
        customer.setId(generateCustomerId());
        return customerRepository.save(customer);
    }

    //Cập nhật thông tin của khách hàng theo customerId
    public Customer updateCustomer(String id, Customer customer) {
        Customer existingCustomer = getCustomerById(id);
        existingCustomer.setName(customer.getName());
        existingCustomer.setPhone(customer.getPhone());
        existingCustomer.setAddress(customer.getAddress());
        existingCustomer.setEmail(customer.getEmail());
        return customerRepository.save(existingCustomer);
    }

    //Xoá khách hoàng theo customerId
    public void deleteCustomer(String id) {
        customerRepository.deleteCustomerById(id);
    }

    //Tìm kiếm khách hàng theo tên hoặc email
    public List<Customer> searchCustomers(String keyword) {
        return customerRepository.searchByNameOrEmail(keyword);
    }

    //Tổng số khách hàng
    public long getTotalCustomers() {
        return customerRepository.countTotalCustomers();
    }

    public List<Order> getOrderHistory(String customerId) {
        getCustomerById(customerId); // Kiểm tra tồn tại
        return orderRepository.findOrdersByCustomerId(customerId);
    }
}
