package com.example.sales.controller;

import com.example.sales.entity.Employee;
import com.example.sales.repository.EmployeeRepository;
import com.example.sales.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    @GetMapping
    List<Employee> getAllEmployee(){
        return employeeService.getAllEmployee();
    }

    @GetMapping("/{employeeId}")
    Employee getEmployeeById(@PathVariable("employeeId") String employeeId){
        return employeeService.getEmployeeById(employeeId);
    }

    @GetMapping("/search")
    List<Employee> searchEmployee(@RequestParam("keyword") String keyword){
        return employeeService.searchEmployees(keyword);
    }

    @GetMapping("/phone")
    List<Employee> searchByPhone(@RequestParam("keyword") String keyword){
        return employeeService.searchByPhone(keyword);
    }

    @PostMapping
    Employee addEmployee(@RequestBody Employee employee){
        return employeeService.addEmployee(employee);
    }

    @PutMapping("/{employeeId}")
    Employee updateEmployee(@RequestBody Employee employee ,@PathVariable("employeeId") String employeeId){
        return  employeeService.updateEmployee(employeeId,employee);
    }

    @DeleteMapping("/{employeeId}")
    String deleteEmployee(@PathVariable("employeeId") String employeeId){
        employeeService.deleteEmployee(employeeId);
        return "Delete employee successfully!";
    }
}
