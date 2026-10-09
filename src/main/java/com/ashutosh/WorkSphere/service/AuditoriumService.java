package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.AuditoriumCreateRequest;
import com.ashutosh.WorkSphere.dto.AuditoriumResponse;
import com.ashutosh.WorkSphere.dto.AuditoriumStatusUpdateRequest;
import com.ashutosh.WorkSphere.dto.AuditoriumUpdateRequest;
import com.ashutosh.WorkSphere.entity.Auditorium;
import com.ashutosh.WorkSphere.entity.Building;
import com.ashutosh.WorkSphere.entity.Campus;
import com.ashutosh.WorkSphere.entity.Floor;
import com.ashutosh.WorkSphere.enums.AuditoriumStatus;
import com.ashutosh.WorkSphere.enums.BuildingStatus;
import com.ashutosh.WorkSphere.enums.CampusStatus;
import com.ashutosh.WorkSphere.enums.FloorStatus;
import com.ashutosh.WorkSphere.exception.DuplicateResourceException;
import com.ashutosh.WorkSphere.exception.InactiveResourceException;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.AuditoriumRepository;
import com.ashutosh.WorkSphere.repository.BuildingRepository;
import com.ashutosh.WorkSphere.repository.FloorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriumService {

    private final AuditoriumRepository auditoriumRepository;
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;

    @Transactional
    public AuditoriumResponse createAuditorium(AuditoriumCreateRequest request) {

        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + request.getBuildingId()));

        Floor floor = floorRepository.findByBuildingIdAndFloorNumber(request.getBuildingId(), request.getFloorNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found"));

        if (floor.getStatus() != FloorStatus.ACTIVE) {
            throw new InactiveResourceException("Cannot create auditorium under an inactive floor");
        }

        if (building.getStatus() != BuildingStatus.ACTIVE) {
            throw new InactiveResourceException("Cannot create auditorium under an inactive building");
        }

        Campus campus = building.getCampus();

        if (campus.getStatus() != CampusStatus.ACTIVE) {
            throw new InactiveResourceException("Cannot create auditorium under an inactive campus");
        }

        if (auditoriumRepository.existsByAuditoriumNameIgnoreCaseAndFloor_Building_Id(request.getAuditoriumName(),request.getBuildingId())){
            throw new DuplicateResourceException("Auditorium with this name already exists in the building");
        }

        String auditoriumCode = generateAuditoriumCode(campus, building, floor);

        Auditorium auditorium = Auditorium.builder()
                .auditoriumCode(auditoriumCode)
                .auditoriumName(request.getAuditoriumName())
                .capacity(request.getCapacity())
                .status(AuditoriumStatus.AVAILABLE)
                .floor(floor)
                .build();

        Auditorium savedAuditorium = auditoriumRepository.save(auditorium);

        return mapToResponse(savedAuditorium);
    }

    @Transactional
    public AuditoriumResponse updateAuditoriumStatus(String auditoriumCode, AuditoriumStatusUpdateRequest request) {

        Auditorium auditorium = auditoriumRepository.findByAuditoriumCode(auditoriumCode)
                .orElseThrow(() -> new ResourceNotFoundException("Auditorium not found with code: " + auditoriumCode));

        auditorium.setStatus(request.getStatus());

        Auditorium updatedAuditorium = auditoriumRepository.save(auditorium);

        return mapToResponse(updatedAuditorium);
    }

    private String generateAuditoriumCode(Campus campus, Building building, Floor floor) {

        String floorCode = getFloorCode(floor.getFloorNumber());

        String prefix = campus.getCode() + "-" + building.getCode() + "-" + floorCode;

        int auditoriumNumber = auditoriumRepository.findAllByFloorId(floor.getId()).size() + 1;

        return String.format("%s-A%02d", prefix, auditoriumNumber);
    }

    private String getFloorCode(Integer floorNumber) {

        if (floorNumber < 0) {
            return "B" + Math.abs(floorNumber);
        }
        return "F" + floorNumber;
    }

    public AuditoriumResponse getAuditoriumByCode(String auditoriumCode){

        Auditorium auditorium = auditoriumRepository.findByAuditoriumCode(auditoriumCode)
                .orElseThrow(()-> new ResourceNotFoundException("Auditorium not found with the give code: "+auditoriumCode));

        return mapToResponse(auditorium);
    }

    public List<AuditoriumResponse> getAllAuditoriumByBuildingCodeAndFloorNumber( String buildingCode, Integer floorNumber){

        List<Auditorium> auditorium = auditoriumRepository.findAllByFloor_Building_CodeAndFloor_FloorNumber(buildingCode,floorNumber);

        return auditorium.stream()
                .map(this::mapToResponse)
                .toList();

    }

    @Transactional
    public AuditoriumResponse updateAuditorium(String auditoriumCode, AuditoriumUpdateRequest request) {

        Auditorium auditorium = auditoriumRepository.findByAuditoriumCode(auditoriumCode)
                .orElseThrow(() -> new ResourceNotFoundException("Auditorium not found with code: " + auditoriumCode));

        boolean nameChanged = !auditorium.getAuditoriumName().equalsIgnoreCase(request.getAuditoriumName());

        if (nameChanged && auditoriumRepository.existsByAuditoriumNameIgnoreCaseAndFloor_Building_Id(
                                request.getAuditoriumName(), auditorium.getFloor().getBuilding().getId())) {

            throw new DuplicateResourceException("Auditorium with this name already exists in the building");
        }

        auditorium.setAuditoriumName(request.getAuditoriumName());
        auditorium.setCapacity(request.getCapacity());

        Auditorium updatedAuditorium = auditoriumRepository.save(auditorium);

        return mapToResponse(updatedAuditorium);
    }


    private AuditoriumResponse mapToResponse(Auditorium auditorium) {

        Floor floor = auditorium.getFloor();
        Building building = floor.getBuilding();
        Campus campus = building.getCampus();

        return AuditoriumResponse
                .builder()
                .id(auditorium.getId())
                .auditoriumCode(auditorium.getAuditoriumCode())
                .auditoriumName(auditorium.getAuditoriumName())
                .capacity(auditorium.getCapacity())
                .status(auditorium.getStatus())
                .floorNumber(floor.getFloorNumber())
                .buildingName(building.getName())
                .buildingCode(building.getCode())
                .campusName(campus.getName())
                .campusCode(campus.getCode())
                .build();
    }
}