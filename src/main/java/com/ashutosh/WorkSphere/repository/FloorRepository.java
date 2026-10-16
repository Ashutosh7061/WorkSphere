package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FloorRepository extends JpaRepository<Floor, Long> {

    Optional<Floor> findByBuildingIdAndFloorNumber(Long buildingId, Integer floorNumber);
    List<Floor> findAllByBuilding_Campus_Code(String campusCode);

    Optional<Floor> findByBuilding_CodeAndBuilding_Campus_CodeAndFloorNumber(String buildingCode, String campusCode, Integer floorNumber);

    boolean existsByBuilding_CodeAndBuilding_Campus_CodeAndFloorNumber(String buildingCode, String campusCode, Integer floorNumber);

    List<Floor> findAllByBuilding_CodeAndBuilding_Campus_Code(String buildingCode, String campusCode);}