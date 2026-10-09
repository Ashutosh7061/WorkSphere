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

    // Find a specific building using both codes
    Optional<Building> findByCodeAndCampus_Code(String buildingCode, String campusCode);

    // Check duplicate building name during updates
    boolean existsByNameAndCampus_Code(String name, String campusCode);

    // Get all buildings belonging to a campus code
    List<Building> findAllByCampus_Code(String campusCode);

}
