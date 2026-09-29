package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomCode(String roomCode);

    long countByFloorId(Long floorId);

    List<Room> findByFloorBuildingIdAndFloorFloorNumber(Long buildingId, Integer floorNumber);


}
