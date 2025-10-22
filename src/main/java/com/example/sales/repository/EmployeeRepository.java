package com.example.sales.repository;

import com.example.sales.entity.Customer;
import com.example.sales.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate; // Assuming hire_date is LocalDate
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {


    @Query(value = "SELECT * FROM Employee", nativeQuery = true)
    List<Employee> getAllEmployee();

    @Query(value = "SELECT * FROM Employee WHERE employee_id = :id", nativeQuery = true)
    Optional<Employee> getEmployeeById(@Param("id") String id);

    @Query(value = "SELECT COUNT(*) FROM Employee WHERE employee_id = :id", nativeQuery = true)
    int countById(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM Employee WHERE employee_id = :id", nativeQuery = true)
    void deleteEmployeeById(@Param("id") String id);

    @Modifying
    @Query(value = """
            INSERT INTO Employee (employee_id, name, position, phone, email, salary, hire_date, address) 
            VALUES (:id, :name, :position, :phone, :email, :salary, :hireDate, :address)
            """, nativeQuery = true)
    void addEmployee(@Param("id") String id,
                              @Param("name") String name,
                              @Param("position") String position,
                              @Param("phone") String phone,
                              @Param("email") String email,
                              @Param("salary") double salary, // Match Entity type
                              @Param("hireDate") LocalDate hireDate, // Match Entity type
                              @Param("address") String address);

    @Modifying
    @Query(value = """
            UPDATE Employee SET 
            name = :name, 
            position = :position, 
            phone = :phone, 
            email = :email, 
            salary = :salary, 
            hire_date = :hireDate, 
            address = :address 
            WHERE employee_id = :id
            """, nativeQuery = true)
    void updateEmployee(@Param("id") String id,
                              @Param("name") String name,
                              @Param("position") String position,
                              @Param("phone") String phone,
                              @Param("email") String email,
                              @Param("salary") double salary, // Match Entity type
                              @Param("hireDate") LocalDate hireDate, // Match Entity type
                              @Param("address") String address);

    @Query(value = "SELECT employee_id FROM Employee ORDER BY CAST(SUBSTRING(employee_id, 2) AS UNSIGNED) DESC", nativeQuery = true)
    List<String> findAllIdsDesc();

    @Query(value = "SELECT * FROM Employee " +
            "WHERE LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(email) LIKE LOWER(CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    List<Employee> searchByNameorEmail(@Param("keyword") String keyword);

    @Query(value="SELECT * FROM Employee WHERE phone = :phone", nativeQuery = true)
    List<Employee> searchByPhone(@Param("phone") String phone);
}