package com.example.sales.controller;

import com.example.sales.entity.Customer;
import com.example.sales.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
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

    @PostMapping
    Customer addCustomer(@RequestBody Customer customer){
        return customerService.addCustomer(customer);
    }

    @PutMapping("/{customerId}")
    Customer updateCustomer(@RequestBody Customer customer, @PathVariable("customerId") Long customerId){
        return customerService.updateCustomer(customerId, customer);
    }

    @GetMapping("/search")
    List<Customer> searchCustomerByName(@RequestParam("keyword") String keyword){
        return customerService.searchCustomers(keyword);
    }

    @DeleteMapping("/{customerId}")
    String deleteCustomer(@PathVariable("customerId") Long customerId){
        customerService.deleteCustomer(customerId);
        return "delete customer successfully";
    }

}
