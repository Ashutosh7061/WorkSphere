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

    public BuildingResponse createBuilding(String campusCode, BuildingCreateRequest request) {

        Campus campus = campusRepository.findByCode(campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with code: " + campusCode));

        if (campus.getStatus() == CampusStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot create building under an inactive campus");
        }

        if (buildingRepository.existsByNameAndCampusId(request.getName(), campus.getId())) {
            throw new DuplicateResourceException("Building already exists with name: " + request.getName());
        }

        String code = request.getCode().trim().toUpperCase();

        if (buildingRepository.existsByCampusIdAndCode(campus.getId(), code)) {
            throw new DuplicateResourceException("Building already exists with code: " + code);
        }

        Building building = Building.builder()
                .name(request.getName())
                .address(request.getAddress())
                .campus(campus)
                .status(BuildingStatus.ACTIVE)
                .code(code)
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

    public BuildingResponse getBuildingByCode(String campusCode, String buildingCode) {
        Building building = buildingRepository.findByCodeAndCampus_Code(buildingCode, campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with code: " + buildingCode + " in campus: " + campusCode));

        return mapToResponse(building);
    }

    public BuildingResponse updateBuilding(String campusCode, String buildingCode, BuildingUpdateRequest request) {

        Building building = buildingRepository.findByCodeAndCampus_Code(buildingCode, campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with code: " + buildingCode + " in campus: " + campusCode));

        if (!building.getName().equalsIgnoreCase(request.getName()) &&
                buildingRepository.existsByNameAndCampus_Code(request.getName(), campusCode)) {

            throw new DuplicateResourceException("Building already exists with name: " + request.getName());
        }
        building.setName(request.getName());
        building.setAddress(request.getAddress());

        Building updatedBuilding = buildingRepository.save(building);

        return mapToResponse(updatedBuilding);

    }

    public BuildingResponse updateBuildingStatus(String campusCode, String buildingCode, BuildingStatus status) {

        Building building = buildingRepository.findByCodeAndCampus_Code(buildingCode,campusCode)
                .orElseThrow(() -> new ResourceNotFoundException( "Building not found with code: " + buildingCode + " in campus: " + campusCode));

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

    public List<BuildingResponse> getBuildingsByCampusCode(String campusCode) {

        Campus campus = campusRepository.findByCode(campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with code: " + campusCode));

        return buildingRepository.findAllByCampusId(campus.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BuildingResponse mapToResponse(Building building) {
        return BuildingResponse.builder()
                .id(building.getId())
                .name(building.getName())
                .code(building.getCode())
                .address(building.getAddress())
                .status(building.getStatus())
                .campusId(building.getCampus().getId())
                .build();
    }
}