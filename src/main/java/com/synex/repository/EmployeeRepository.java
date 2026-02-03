package com.synex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synex.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
    Optional<Employee> findByEmail(String email);

    List<Employee> findByDepartment(String department);

    List<Employee> findByManagerId(Long managerId);

    boolean existsByEmail(String email);
}
