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
import com.ashutosh.WorkSphere.repository.FloorRepository;
import com.ashutosh.WorkSphere.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.ashutosh.WorkSphere.entity.User;
import com.ashutosh.WorkSphere.enums.Role;
import com.ashutosh.WorkSphere.security.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;

    @Transactional
    public RoomResponse createRoom(String campusCode, String buildingCode, RoomCreateRequest request) {

        Floor floor = floorRepository.findByBuilding_CodeAndBuilding_Campus_CodeAndFloorNumber(buildingCode,campusCode, request.getFloorNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Floor " + request.getFloorNumber() + " not found in building: " + buildingCode + " on campus: " + campusCode));

        validateBuildingAccess(floor.getBuilding());

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

        validateBuildingAccess(room.getFloor().getBuilding());

        return mapToResponse(room);
    }

    public List<RoomResponse> getRoomsByFloor(String campusCode, String buildingCode, Integer floorNumber) {

        Floor floor = floorRepository
                .findByBuilding_CodeAndBuilding_Campus_CodeAndFloorNumber(buildingCode, campusCode, floorNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Floor " + floorNumber + " not found in building: " + buildingCode + " on campus: " + campusCode));

        validateBuildingAccess(floor.getBuilding());

        List<Room> rooms = roomRepository
                .findByFloorBuildingCodeAndFloorBuildingCampusCodeAndFloorFloorNumber(buildingCode, campusCode, floorNumber);

        return rooms.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public RoomResponse updateRoomDetails(String roomCode, RoomUpdateRequest request){
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found with code: "+ roomCode));

        validateBuildingAccess(room.getFloor().getBuilding());

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

        validateBuildingAccess(room.getFloor().getBuilding());

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

    private void validateBuildingAccess(Building building) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new AccessDeniedException("User is not authenticated");
        }
        User user = userDetails.getUser();

        // SUPER_ADMIN has global access
        if (user.getRole() == Role.SUPER_ADMIN) {
            return;
        }

        // CAMPUS_ADMIN can access buildings within assigned campus
        if (user.getRole() == Role.CAMPUS_ADMIN) {
            if (user.getCampus() != null && user.getCampus().getId().equals(building.getCampus().getId())) {
                return;
            }
        }

        // BUILDING_ADMIN can access only assigned building
        if (user.getRole() == Role.BUILDING_ADMIN) {

            if (user.getBuilding() != null && user.getBuilding().getId().equals(building.getId()) &&
                    user.getCampus() != null && user.getCampus().getId().equals(building.getCampus().getId()))
            {
                return;
            }
        }
        throw new AccessDeniedException("You are not authorized to access this building");
    }
}
