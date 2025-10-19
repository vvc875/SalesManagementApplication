package com.example.sales.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sales.entity.Employee;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    //Lấy danh sách tất cả nhân viên
    @Query(value = "SELECT * FROM employee", nativeQuery = true)
    List<Employee> findAll();

    //Lấy nhân viên theo id
    @Query(value = "SELECT * FROM employee WHERE employee_id = :employeeId", nativeQuery = true)
    Optional<Employee> findById(@Param("employeeId") String employeeId);

    // ID tu dong
    @Query(value = "SELECT employee_id FROM employee ORDER BY employee_id DESC", nativeQuery = true)
    List<String> findAllIdsDesc();

    //Thêm một nhân viên mới

    //Xoá thông tin nhân viên
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM employee WHERE employee_id = :id", nativeQuery = true)
    void deleteById(@Param("id") String id);

}