package com.example.sales.controller;

import com.example.sales.entity.Customer;
import com.example.sales.entity.Order;
import com.example.sales.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "*")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    List<Customer> getAllCustomer(){
        return customerService.getAllCustomer();
    }

    @GetMapping("/count")
    long getTotalCustomer(){
        return customerService.getTotalCustomers();
    }

    @GetMapping("/{customerId}/order")
    List<Order> getCustomerOrder(@PathVariable("customerId") String customerId){
        return customerService.getOrderHistory(customerId);
    }

    @PostMapping
    Customer addCustomer(@RequestBody Customer customer){

        return customerService.addCustomer(customer);
    }

    @PutMapping("/{customerId}")
    Customer updateCustomer(@RequestBody Customer customer, @PathVariable("customerId") String customerId){
        return customerService.updateCustomer(customerId, customer);
    }

    @GetMapping("/search")
    List<Customer> searchCustomerByName(@RequestParam("keyword") String keyword){
        return customerService.searchCustomers(keyword);
    }

    @GetMapping("/phone")
    List<Customer> searchCustomerByPhone(@RequestParam("keyword") String keyword){
        return customerService.searchCustomerByPhone(keyword);
    }

    @GetMapping("/{customerId}")
    public Customer getCustomerById(@PathVariable("customerId") String customerId) {
        return customerService.getCustomerById(customerId);
    }

    @DeleteMapping("/{customerId}")
    String deleteCustomer(@PathVariable("customerId") String customerId){
        customerService.deleteCustomer(customerId);
        return "Delete customer successfully!";
    }

}