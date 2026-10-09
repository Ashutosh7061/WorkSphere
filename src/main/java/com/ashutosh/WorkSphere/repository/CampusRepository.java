package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Campus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampusRepository extends JpaRepository<Campus, Long> {

    boolean existsByNameAndCompanyId(String name, Long companyId);

    Optional<Campus> findByIdAndCompanyId(Long id, Long companyId);

    boolean existsByCode(String code);

    Optional<Campus> findByCode(String code);
}