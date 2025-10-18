package com.example.sales.service;

import com.example.sales.entity.Employee;
import com.example.sales.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    //Lấy danh sách tất cả nhân viên
    public List<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

    //Lấy nhân viên theo id
    public Employee getEmployeeById(String employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
    }

    private String generateEmployeeId() {
        List<String> ids = employeeRepository.findAllIdsDesc();
        if (ids.isEmpty()) return "E001";

        String lastId = ids.get(0); // Lấy ID lớn nhất hiện tại
        int num = Integer.parseInt(lastId.replace("E", ""));
        return String.format("E%03d", num + 1);
    }

    //Thêm một nhân viên mới
    public Employee addEmployee(Employee employee) {
        employee.setId(generateEmployeeId());
        return employeeRepository.save(employee);
    }

    //Cập nhật thông tin nhân viên
    public Employee updateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    //Xoá thông tin nhân viên
    public void deleteEmployee(String employeeId) {
        employeeRepository.deleteById(employeeId);
    }
}
