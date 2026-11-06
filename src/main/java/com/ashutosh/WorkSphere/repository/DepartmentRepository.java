package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByNameIgnoreCaseAndCampus_Id(String name, Long campusId);

    boolean existsByDepartmentCodeIgnoreCaseAndCampus_Id(String departmentCode, Long campusId);

    Optional<Department> findByGeneratedCode(String generatedCode);

    List<Department> findAllByCampus_Id(Long campusId);
}