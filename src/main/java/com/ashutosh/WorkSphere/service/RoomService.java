package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.RoomCreateRequest;
import com.ashutosh.WorkSphere.dto.RoomResponse;
import com.ashutosh.WorkSphere.dto.RoomUpdateRequest;
import com.ashutosh.WorkSphere.entity.Building;
import com.ashutosh.WorkSphere.entity.Campus;
import com.ashutosh.WorkSphere.entity.Floor;
import com.ashutosh.WorkSphere.entity.Room;
import com.ashutosh.WorkSphere.enums.BuildingStatus;
import com.ashutosh.WorkSphere.enums.CampusStatus;
import com.ashutosh.WorkSphere.enums.FloorStatus;
import com.ashutosh.WorkSphere.enums.RoomStatus;
import com.ashutosh.WorkSphere.exception.InactiveResourceException;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.BuildingRepository;
import com.ashutosh.WorkSphere.repository.FloorRepository;
import com.ashutosh.WorkSphere.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;

    @Transactional
    public RoomResponse createRoom(RoomCreateRequest request) {

        Floor floor = floorRepository.findByBuildingIdAndFloorNumber(request.getBuildingId(), request.getFloorNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Floor " + request.getFloorNumber() + " not found in building with id: " + request.getBuildingId()));

        Campus campus = floor.getBuilding().getCampus();
        if(campus.getStatus() == CampusStatus.INACTIVE){
            throw new InactiveResourceException("Cannot create room under an inactive campus");
        }

        Building building = floor.getBuilding();
        if (building.getStatus() == BuildingStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot create room under an inactive building");
        }

        if (floor.getStatus() == FloorStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot create room under an inactive floor");
        }

        long roomCount = roomRepository.countByFloorId(floor.getId());

        String roomCode = floor.getBuilding().getCampus().getCode()
                + "-"
                + floor.getBuilding().getCode()
                + "-F"
                + String.format("%02d", floor.getFloorNumber())
                + "-R"
                + String.format("%02d", roomCount + 1);

        Room room = Room.builder()
                .roomCode(roomCode)
                .roomName(request.getRoomName())
                .roomType(request.getRoomType())
                .capacity(request.getCapacity())
                .status(RoomStatus.AVAILABLE)
                .floor(floor)
                .build();

        Room savedRoom = roomRepository.save(room);

        return mapToResponse(savedRoom);
    }

    public RoomResponse getRoomByCode(String roomCode){

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found with code: "+roomCode));

        return mapToResponse(room);
    }

    public List<RoomResponse> getRoomsByFloor(Long buildingId, Integer floorNumber) {

        List<Room> rooms = roomRepository.findByFloorBuildingIdAndFloorFloorNumber(buildingId, floorNumber);

        return rooms.stream()
                .map(this::mapToResponse)
                .toList();

    }

    @Transactional
    public RoomResponse updateRoomDetails(String roomCode, RoomUpdateRequest request){
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found with code: "+ roomCode));

        room.setRoomName(request.getRoomName());
        room.setRoomType(request.getRoomType());
        room.setCapacity(request.getCapacity());

        Room updatedRoom = roomRepository.save(room);

        return mapToResponse(updatedRoom);
    }

    @Transactional
    public RoomResponse updateRoomStatus(String roomCode, RoomStatus status) {

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with code: " + roomCode));

        room.setStatus(status);
        Room updatedRoom = roomRepository.save(room);

        return mapToResponse(updatedRoom);
    }

    private RoomResponse mapToResponse(Room room) {

        Floor floor = room.getFloor();
        Building building = floor.getBuilding();
        Campus campus = building.getCampus();

        return RoomResponse.builder()
                .id(room.getId())
                .roomCode(room.getRoomCode())
                .roomName(room.getRoomName())
                .roomType(room.getRoomType())
                .capacity(room.getCapacity())
                .status(room.getStatus())
                .floorNumber(floor.getFloorNumber())
                .buildingName(building.getName())
                .buildingCode(building.getCode())
                .campusName(campus.getName())
                .campusCode(campus.getCode())
                .build();
    }
}
