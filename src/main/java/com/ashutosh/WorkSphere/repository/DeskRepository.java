package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Desk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeskRepository extends JpaRepository<Desk, Long> {

    int countByFloorId(Long floorId);

    List<Desk> findByFloorBuildingIdAndFloorFloorNumber(Long buildingId, Integer floorNumber);

    Optional<Desk> findByDeskCode(String deskCode);

    List<Desk> findByDeskCodeIn(List<String> deskCodes);


}
