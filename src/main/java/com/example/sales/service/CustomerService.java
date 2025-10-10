package com.example.sales.service;

import com.example.sales.entity.Customer;
import com.example.sales.entity.Product;
import com.example.sales.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomer(){
        return customerRepository.findAll();
    }

    public Customer getCustomerById(long id){
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy khach hang!"));
    }

    public Customer addCustomer(Customer customer){
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customer) {
        Customer existingCustomer = getCustomerById(id);
        existingCustomer.setName(customer.getName());
        existingCustomer.setPhone(customer.getPhone());
        existingCustomer.setAddress(customer.getAddress());
        existingCustomer.setEmail(customer.getEmail());
        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(long customerId){
        customerRepository.deleteById(customerId);
    }

    // Tìm kiếm khách hàng theo tên hoặc email
    public List<Customer> searchCustomers(String keyword) {
        return customerRepository.searchByNameOrEmail(keyword);
    }

    // Đếm tổng số khách hàng
    public long getTotalCustomers() {
        return customerRepository.count();
    }

}
