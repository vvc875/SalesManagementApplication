package com.example.sales.service;

import com.example.sales.entity.Customer;
import com.example.sales.entity.Employee;
import com.example.sales.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    // Lấy danh sách tất cả nhân viên
    public List<Employee> getAllEmployee() {

        return employeeRepository.getAllEmployee();
    }

    // Lấy nhân viên theo id
    public Employee getEmployeeById(String employeeId) {
        return employeeRepository.getEmployeeById(employeeId).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
    }

    // Sinh employeeId
    private String generateEmployeeId() {
        List<String> ids = employeeRepository.findAllIdsDesc();
        if (ids.isEmpty()) return "E001";

        String lastId = ids.get(0); // Lấy ID lớn nhất hiện tại
        int num = Integer.parseInt(lastId.replace("E", ""));
        return String.format("E%03d", num + 1);
    }

    // Thêm một nhân viên mới
    @Transactional
    public Employee addEmployee(Employee employee) {
        String newId = generateEmployeeId();
        employee.setId(newId); // Dùng newId

        employeeRepository.addEmployee(
                employee.getId(), // Dùng newId
                employee.getName(),
                employee.getPosition(),
                employee.getPhone(),
                employee.getEmail(),
                employee.getSalary(),
                employee.getHireDate(),
                employee.getAddress()
        );

        return getEmployeeById(newId); // Dùng newId
    }

    // Cập nhật thông tin nhân viên
    @Transactional
    public Employee updateEmployee(String employeeId, Employee employeeDetails) {
        if (employeeRepository.countById(employeeId) == 0) {
            throw new RuntimeException("Không tìm thấy nhân viên để cập nhật!");
        }

        employeeRepository.updateEmployee(
                employeeId,
                employeeDetails.getName(),
                employeeDetails.getPosition(),
                employeeDetails.getPhone(),
                employeeDetails.getEmail(),
                employeeDetails.getSalary(),
                employeeDetails.getHireDate(),
                employeeDetails.getAddress()
        );

        return getEmployeeById(employeeId);
    }

    // Xoá thông tin nhân viên
    @Transactional
    public void deleteEmployee(String employeeId) {
        if (employeeRepository.countById(employeeId) == 0) {
            throw new RuntimeException("Không tìm thấy nhân viên để xóa!");
        }
        employeeRepository.deleteEmployeeById(employeeId);
    }

    // Tìm kiếm nhân viên theo tên or email
    public List<Employee> searchEmployees(String keyword){
        return employeeRepository.searchByNameorEmail(keyword);
    }

    //Tìm kiém nhân viên theo phone
    public List<Employee> searchByPhone(String phone){
        return employeeRepository.searchByPhone(phone);
    }
}