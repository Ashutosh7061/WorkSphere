package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FloorRepository extends JpaRepository<Floor, Long> {

    Optional<Floor> findByBuildingIdAndFloorNumber(Long buildingId, Integer floorNumber);

    boolean existsByBuildingIdAndFloorNumber(Long buildingId, Integer floorNumber);

    List<Floor> findAllByBuildingId(Long buildingId);
}