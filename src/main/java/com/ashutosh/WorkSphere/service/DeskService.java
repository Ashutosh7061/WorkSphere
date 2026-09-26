package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.DeskBulkCreateResponse;
import com.ashutosh.WorkSphere.dto.DeskCreateRequest;
import com.ashutosh.WorkSphere.dto.DeskResponse;
import com.ashutosh.WorkSphere.dto.DeskStatusUpdateRequest;
import com.ashutosh.WorkSphere.entity.Building;
import com.ashutosh.WorkSphere.entity.Campus;
import com.ashutosh.WorkSphere.entity.Desk;
import com.ashutosh.WorkSphere.entity.Floor;
import com.ashutosh.WorkSphere.enums.BuildingStatus;
import com.ashutosh.WorkSphere.enums.CampusStatus;
import com.ashutosh.WorkSphere.enums.DeskStatus;
import com.ashutosh.WorkSphere.enums.FloorStatus;
import com.ashutosh.WorkSphere.exception.InactiveResourceException;
import com.ashutosh.WorkSphere.exception.InvalidRequestArguments;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.BuildingRepository;
import com.ashutosh.WorkSphere.repository.DeskRepository;
import com.ashutosh.WorkSphere.repository.FloorRepository;
import lombok.RequiredArgsConstructor;
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
    public DeskBulkCreateResponse createDesks(Long buildingId, DeskCreateRequest request) {

        Building building = buildingRepository.findById(buildingId).orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + buildingId));

        Campus campus = building.getCampus();

        if (campus.getStatus() == CampusStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot create desks under an inactive campus");
        }

        if (building.getStatus() == BuildingStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot create desks under an inactive building");
        }

        Floor floor = floorRepository.findByBuildingIdAndFloorNumber(buildingId, request.getFloorNumber()).orElseThrow(() -> new ResourceNotFoundException("Floor not found with number: " + request.getFloorNumber() + " in building: " + buildingId));

        if (floor.getStatus() == FloorStatus.INACTIVE) {
            throw new InactiveResourceException("Cannot create desks under an inactive floor");
        }

        List<Desk> desks = new ArrayList<>();

        int startNumber = deskRepository.countByFloorId(floor.getId()) + 1;

        for (int i = 0; i < request.getQuantity(); i++) {
            int deskNumber = startNumber + i;

            String deskCode = generateDeskCode(campus, building, floor, deskNumber);

            Desk desk = Desk.builder().deskCode(deskCode).status(DeskStatus.AVAILABLE).floor(floor).build();

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

    public List<DeskResponse> getDesksByBuildingAndFloor(Long buildingId, Integer floorNumber) {

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + buildingId));

        Floor floor = floorRepository.findByBuildingIdAndFloorNumber(buildingId, floorNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found with number: " + floorNumber + " in building: " + buildingId));

        return deskRepository
                .findByFloorBuildingIdAndFloorFloorNumber(buildingId, floorNumber)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DeskResponse getDeskByCode(String deskCode){

        Desk desk = deskRepository.findByDeskCode(deskCode)
                .orElseThrow(()-> new ResourceNotFoundException("Desk not found with code: "+ deskCode));

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
}
