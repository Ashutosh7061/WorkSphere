package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    Optional<Building> findByNameAndCampusId(String name, Long campusId);

    boolean existsByNameAndCampusId(String name, Long campusId);

    List<Building> findAllByCampusId(Long campusId);

    boolean existsByCampusIdAndCode(Long campusId, String code);

}
