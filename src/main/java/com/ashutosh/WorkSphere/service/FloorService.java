package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.*;
import com.ashutosh.WorkSphere.entity.Building;
import com.ashutosh.WorkSphere.entity.Campus;
import com.ashutosh.WorkSphere.entity.Floor;
import com.ashutosh.WorkSphere.enums.BuildingStatus;
import com.ashutosh.WorkSphere.enums.FloorStatus;
import com.ashutosh.WorkSphere.exception.DuplicateResourceException;
import com.ashutosh.WorkSphere.exception.InactiveResourceException;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.BuildingRepository;
import com.ashutosh.WorkSphere.repository.FloorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FloorService {

    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;

    public FloorResponse createFloor(FloorCreateRequest request) {
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + request.getBuildingId()));

        if (building.getStatus() == BuildingStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot create floor under an inactive building");
        }

        if (floorRepository.existsByBuildingIdAndFloorNumber(request.getBuildingId(), request.getFloorNumber())) {
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

    public FloorDetailResponse getFloorById(Long id) {

        Floor floor = floorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Floor not found with id: " + id));

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

    public FloorUpdateResponse updateFloor(Long id, FloorUpdateRequest request) {

        Floor floor = floorRepository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException("Floor not found with id: " + id));

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

    public FloorResponse updateFloorStatus(Long id, FloorStatus status) {

        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found with id: " + id));

        floor.setStatus(status);

        Floor updatedFloor = floorRepository.save(floor);

        return mapToResponse(updatedFloor);
    }

    public List<FloorResponse> getFloorsByBuildingId(Long buildingId) {

        buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + buildingId));

        return floorRepository.findAllByBuildingId(buildingId)
                .stream()
                .map(this::mapToResponse)
                .toList();
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
