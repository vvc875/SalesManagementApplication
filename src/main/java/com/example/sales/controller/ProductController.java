package com.example.sales.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @GetMapping("/hello")
    public String hello(){
        return "hello spring boot";
    }
}
