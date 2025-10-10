package com.example.sales.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
//    @GetMapping("/product/search")
    public String searchProductPage() {
        return "product_search";  // Trả về file product_search.html trong /templates
    }
}
