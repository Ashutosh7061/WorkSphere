package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByName(String name);

    Optional<Company> findFirstByOrderByIdAsc();


}