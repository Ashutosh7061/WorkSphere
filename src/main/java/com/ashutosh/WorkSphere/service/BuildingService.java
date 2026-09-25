package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.BuildingCreateRequest;
import com.ashutosh.WorkSphere.dto.BuildingResponse;
import com.ashutosh.WorkSphere.dto.BuildingUpdateRequest;
import com.ashutosh.WorkSphere.entity.Building;
import com.ashutosh.WorkSphere.entity.Campus;
import com.ashutosh.WorkSphere.enums.BuildingStatus;
import com.ashutosh.WorkSphere.enums.CampusStatus;
import com.ashutosh.WorkSphere.exception.DuplicateResourceException;
import com.ashutosh.WorkSphere.exception.InactiveResourceException;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.BuildingRepository;
import com.ashutosh.WorkSphere.repository.CampusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final CampusRepository campusRepository;

    public BuildingResponse createBuilding(BuildingCreateRequest request) {

        Campus campus = campusRepository.findById(request.getCampusId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with id: " + request.getCampusId()));

        if (campus.getStatus() == CampusStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot create building under an inactive campus");
        }

        if (buildingRepository.existsByNameAndCampusId(request.getName(), request.getCampusId())) {
            throw new DuplicateResourceException("Building already exists with name: " + request.getName());
        }

        Building building = Building.builder()
                .name(request.getName())
                .address(request.getAddress())
                .campus(campus)
                .status(BuildingStatus.ACTIVE)
                .build();

        Building savedBuilding = buildingRepository.save(building);

        return mapToResponse(savedBuilding);
    }

    public List<BuildingResponse> getAllBuildings() {

        return buildingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public BuildingResponse getBuildingById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));

        return mapToResponse(building);
    }

    public BuildingResponse updateBuilding(Long id, BuildingUpdateRequest request) {

        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));

        if (!building.getName().equalsIgnoreCase(request.getName()) &&
                buildingRepository.existsByNameAndCampusId(request.getName(), building.getCampus().getId())) {

            throw new DuplicateResourceException("Building already exists with name: " + request.getName());
        }
        building.setName(request.getName());
        building.setAddress(request.getAddress());

        Building updatedBuilding = buildingRepository.save(building);

        return mapToResponse(updatedBuilding);

    }

    public BuildingResponse updateBuildingStatus(Long id, BuildingStatus status) {

        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));

        building.setStatus(status);
        Building updatedBuilding = buildingRepository.save(building);

        return mapToResponse(updatedBuilding);
    }

    public List<BuildingResponse> getBuildingsByCampusId(Long campusId) {

        campusRepository.findById(campusId)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with id: " + campusId));

        return buildingRepository.findAllByCampusId(campusId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BuildingResponse mapToResponse(Building building) {
        return BuildingResponse.builder()
                .id(building.getId())
                .name(building.getName())
                .address(building.getAddress())
                .status(building.getStatus())
                .campusId(building.getCampus().getId())
                .build();
    }
}