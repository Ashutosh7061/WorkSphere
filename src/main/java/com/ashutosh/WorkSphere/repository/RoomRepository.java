package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomCode(String roomCode);

    long countByFloorId(Long floorId);

    List<Room> findByFloorBuildingCodeAndFloorBuildingCampusCodeAndFloorFloorNumber(String buildingCode, String campusCode, Integer floorNumber);

    List<Room> findByFloor_Building_CodeAndFloor_Building_Campus_CodeAndFloor_FloorNumber(String buildingCode, String campusCode, Integer floorNumber);

    Optional<Room> findByRoomCodeAndFloor_FloorNumberAndFloor_Building_CodeAndFloor_Building_Campus_Code(String roomCode, Integer floorNumber, String buildingCode, String campusCode);
}
