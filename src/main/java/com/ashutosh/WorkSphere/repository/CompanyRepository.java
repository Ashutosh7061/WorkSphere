package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByName(String name);
}