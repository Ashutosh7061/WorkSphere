package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.DeskBulkCreateResponse;
import com.ashutosh.WorkSphere.dto.DeskCreateRequest;
import com.ashutosh.WorkSphere.dto.DeskResponse;
import com.ashutosh.WorkSphere.dto.DeskStatusUpdateRequest;
import com.ashutosh.WorkSphere.entity.*;
import com.ashutosh.WorkSphere.enums.*;
import com.ashutosh.WorkSphere.exception.InactiveResourceException;
import com.ashutosh.WorkSphere.exception.InvalidRequestArguments;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.BuildingRepository;
import com.ashutosh.WorkSphere.repository.DeskRepository;
import com.ashutosh.WorkSphere.repository.FloorRepository;
import com.ashutosh.WorkSphere.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeskService {

    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final DeskRepository deskRepository;

    @Transactional
    public DeskBulkCreateResponse createDesks(String campusCode,String buildingCode, DeskCreateRequest request) {

        Building building = buildingRepository.findByCodeAndCampus_Code(buildingCode,campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Building " + buildingCode + " not found in campus " + campusCode));

        validateBuildingAccess(building);

        Campus campus = building.getCampus();

        if (campus.getStatus() == CampusStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot create desks under an inactive campus");
        }

        if (building.getStatus() == BuildingStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot create desks under an inactive building");
        }

        Floor floor = floorRepository.findByBuilding_CodeAndBuilding_Campus_CodeAndFloorNumber(buildingCode,campusCode, request.getFloorNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found with number: " + request.getFloorNumber() + " in building: " + buildingCode));

        if (floor.getStatus() == FloorStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot create desks under an inactive floor");
        }

        List<Desk> desks = new ArrayList<>();

        int startNumber = (int)deskRepository.countByFloorId(floor.getId()) + 1;

        for (int i = 0; i < request.getQuantity(); i++) {
            int deskNumber = startNumber + i;

            String deskCode = generateDeskCode(campus, building, floor, deskNumber);

            Desk desk = Desk.builder()
                    .deskCode(deskCode)
                    .status(DeskStatus.AVAILABLE)
                    .floor(floor)
                    .build();

            desks.add(desk);
        }

        List<Desk> savedDesks = deskRepository.saveAll(desks);

        return DeskBulkCreateResponse.builder().message(savedDesks.size() + " desks created successfully.").quantity(savedDesks.size()).campusName(campus.getName()).campusCode(campus.getCode()).buildingName(building.getName()).buildingCode(building.getCode()).floorNumber(floor.getFloorNumber()).startDeskCode(savedDesks.get(0).getDeskCode()).endDeskCode(savedDesks.get(savedDesks.size() - 1).getDeskCode()).build();
    }

    private String generateDeskCode(Campus campus, Building building, Floor floor, int deskNumber) {

        String floorCode;

        if (floor.getFloorNumber() < 0) {
            floorCode = "B" + Math.abs(floor.getFloorNumber());
        } else {
            floorCode = "F" + floor.getFloorNumber();
        }

        return campus.getCode() + "-" + building.getCode() + "-" + floorCode + "-D" + String.format("%02d", deskNumber);
    }

    public List<DeskResponse> getDesksByBuildingAndFloor(String campusCode, String buildingCode, Integer floorNumber) {

        Floor floor = floorRepository
                .findByBuilding_CodeAndBuilding_Campus_CodeAndFloorNumber(buildingCode, campusCode, floorNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found: " + floorNumber + " in building: " + buildingCode + " on campus: " + campusCode));

        validateBuildingAccess(floor.getBuilding());

        return deskRepository
                .findByFloorBuildingCodeAndFloorBuildingCampusCodeAndFloorFloorNumber(buildingCode, campusCode, floorNumber)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DeskResponse getDeskByCode(String deskCode){

        Desk desk = deskRepository.findByDeskCode(deskCode)
                .orElseThrow(()-> new ResourceNotFoundException("Desk not found with code: "+ deskCode));

        validateBuildingAccess(desk.getFloor().getBuilding());

        return mapToResponse(desk);
    }

    @Transactional
    public String updateDeskStatus(DeskStatusUpdateRequest request) {

        if (request.getStartDeskNumber() > request.getEndDeskNumber()) {
            throw new InvalidRequestArguments("Start desk number cannot be greater than end desk number");
        }

        List<String> deskCodes = new ArrayList<>();

        for (int number = request.getStartDeskNumber(); number <= request.getEndDeskNumber(); number++) {

            String deskCode = request.getDeskPrefix() + "-D" + String.format("%02d", number);

            deskCodes.add(deskCode);
        }

        System.out.println("Generated desk codes: " + deskCodes);

        List<Desk> desks = deskRepository.findByDeskCodeIn(deskCodes);

        if (desks.size() != deskCodes.size()) {
            throw new ResourceNotFoundException("One or more desks in the specified range were not found");
        }

        for (Desk desk : desks) {
            validateBuildingAccess(desk.getFloor().getBuilding());
        }

        desks.forEach(desk -> desk.setStatus(request.getStatus()));

        deskRepository.saveAll(desks);

        return desks.size() + " desks updated successfully";
    }

    private DeskResponse mapToResponse(Desk desk) {

        Floor floor = desk.getFloor();
        Building building = floor.getBuilding();
        Campus campus = building.getCampus();

        return DeskResponse
                .builder()
                .id(desk.getId())
                .deskCode(desk.getDeskCode())
                .status(desk.getStatus())
                .floorId(floor.getId())
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
