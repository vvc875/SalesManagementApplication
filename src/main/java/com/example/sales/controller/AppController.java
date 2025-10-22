package com.example.sales.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/page_home")
    public String showHomePage(){

        return "home";
    }

    @GetMapping("/page_product")
    public String showProductPage() {

        return "product_search";
    }

    @GetMapping("/page_category")
    public String showCategoryPage() {

        return "category_search";
    }

    @GetMapping("/page_customer")
    public String showCustomerPage() {

        return "customer_search";
    }

    @GetMapping("/page_employee")
    public String showEmployeePage() {

        return "employee_search";
    }
}