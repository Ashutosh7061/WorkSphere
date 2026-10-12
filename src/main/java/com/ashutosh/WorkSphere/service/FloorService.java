package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.*;
import com.ashutosh.WorkSphere.entity.Building;
import com.ashutosh.WorkSphere.entity.Campus;
import com.ashutosh.WorkSphere.entity.Floor;
import com.ashutosh.WorkSphere.enums.BuildingStatus;
import com.ashutosh.WorkSphere.enums.CampusStatus;
import com.ashutosh.WorkSphere.enums.FloorStatus;
import com.ashutosh.WorkSphere.exception.DuplicateResourceException;
import com.ashutosh.WorkSphere.exception.InactiveResourceException;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.BuildingRepository;
import com.ashutosh.WorkSphere.repository.CampusRepository;
import com.ashutosh.WorkSphere.repository.FloorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FloorService {

    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final CampusRepository campusRepository;

    public FloorResponse createFloor(String campusCode, String buildingCode, FloorCreateRequest request) {

        Building building = findBuilding(campusCode, buildingCode);


        if (building.getStatus() == BuildingStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot create floor under an inactive building");
        }

        if (floorRepository.existsByBuilding_CodeAndBuilding_Campus_CodeAndFloorNumber(
                buildingCode, campusCode, request.getFloorNumber())) {
            throw new DuplicateResourceException("Floor number " + request.getFloorNumber() + " already exists in this building");
        }

        Floor floor = Floor.builder()
                .floorNumber(request.getFloorNumber())
                .name(request.getName())
                .building(building)
                .status(FloorStatus.ACTIVE)
                .build();

        Floor savedFloor = floorRepository.save(floor);

        return mapToResponse(savedFloor);
    }

    public List<FloorResponse> getAllFloors() {
        return floorRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public FloorDetailResponse getFloorById(String campusCode, String buildingCode, Integer floorNumber) {

        Floor floor = findFloor(campusCode, buildingCode, floorNumber);
        Building building = floor.getBuilding();
        Campus campus = building.getCampus();

        return FloorDetailResponse.builder()
                .id(floor.getId())
                .floorNumber(floor.getFloorNumber())
                .name(floor.getName())
                .status(floor.getStatus())
                .buildingId(building.getId())
                .buildingName(building.getName())
                .campusId(campus.getId())
                .campusName(campus.getName())
                .build();
    }

    public FloorUpdateResponse updateFloor(String campusCode, String buildingCode,
                                           Integer floorNumber, FloorUpdateRequest request) {

        Floor floor = findFloor(campusCode, buildingCode,floorNumber);

        String oldName = floor.getName();

        floor.setName(request.getName());

        Floor updatedFloor = floorRepository.save(floor);

        return FloorUpdateResponse.builder()
                .id(updatedFloor.getId())
                .oldName(oldName)
                .newName(updatedFloor.getName())
                .buildingName(updatedFloor.getBuilding().getName())
                .build();
    }

    public FloorResponse updateFloorStatus(String campusCode, String buildingCode,
                                           Integer floorNumber, FloorStatus status) {

        Floor floor = findFloor(campusCode,buildingCode,floorNumber);

        floor.setStatus(status);

        Floor updatedFloor = floorRepository.save(floor);

        return mapToResponse(updatedFloor);
    }

    public List<FloorResponse> getFloorsByBuildingId(String campusCode, String buildingCode) {

        findBuilding(campusCode, buildingCode);

        return floorRepository.findAllByBuilding_CodeAndBuilding_Campus_Code(buildingCode,campusCode)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<FloorResponse> getFloorsByCampusCode(String campusCode) {

        campusRepository.findByCode(campusCode).orElseThrow(() -> new ResourceNotFoundException("Campus not found with code: " + campusCode));

        return floorRepository.findAllByBuilding_Campus_Code(campusCode)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Building findBuilding(String campusCode, String buildingCode) {
        Campus campus = campusRepository.findByCode(campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with code: " + campusCode));

        if (campus.getStatus() == CampusStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot operate on a building under an inactive campus");
        }

        return buildingRepository.findByCodeAndCampus_Code(buildingCode, campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with code: " + buildingCode + " in campus: " + campusCode));
    }

    private Floor findFloor(String campusCode, String buildingCode, Integer floorNumber) {

        findBuilding(campusCode, buildingCode);

        return floorRepository
                .findByBuilding_CodeAndBuilding_Campus_CodeAndFloorNumber(buildingCode, campusCode, floorNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Floor number " + floorNumber + " not found in building " + buildingCode + " in campus " + campusCode));
    }

    private FloorResponse mapToResponse(Floor floor) {
        return FloorResponse.builder()
                .id(floor.getId())
                .floorNumber(floor.getFloorNumber())
                .name(floor.getName())
                .status(floor.getStatus())
                .buildingId(floor.getBuilding().getId())
                .buildingName(floor.getBuilding().getName())
                .build();
    }
}
