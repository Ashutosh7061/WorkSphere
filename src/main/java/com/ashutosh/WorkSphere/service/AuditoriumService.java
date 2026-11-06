package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.AuditoriumCreateRequest;
import com.ashutosh.WorkSphere.dto.AuditoriumResponse;
import com.ashutosh.WorkSphere.dto.AuditoriumStatusUpdateRequest;
import com.ashutosh.WorkSphere.dto.AuditoriumUpdateRequest;
import com.ashutosh.WorkSphere.entity.*;
import com.ashutosh.WorkSphere.enums.*;
import com.ashutosh.WorkSphere.exception.DuplicateResourceException;
import com.ashutosh.WorkSphere.exception.InactiveResourceException;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.AuditoriumRepository;
import com.ashutosh.WorkSphere.repository.BuildingRepository;
import com.ashutosh.WorkSphere.repository.CampusRepository;
import com.ashutosh.WorkSphere.repository.FloorRepository;
import com.ashutosh.WorkSphere.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriumService {

    private final AuditoriumRepository auditoriumRepository;
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final CampusRepository campusRepository;

    @Transactional
    public AuditoriumResponse createAuditorium(String campusCode, String buildingCode, AuditoriumCreateRequest request) {

        Building building = buildingRepository.findByCodeAndCampus_Code(buildingCode, campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Building " + buildingCode + " not found in campus " + campusCode));

        validateBuildingAccess(building);

        Floor floor = floorRepository.findByBuilding_CodeAndBuilding_Campus_CodeAndFloorNumber(buildingCode,campusCode, request.getFloorNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Floor " + request.getFloorNumber() + " not found in building " + buildingCode + " on campus " + campusCode));

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

        if (auditoriumRepository.existsByAuditoriumNameIgnoreCaseAndFloor_Building_Id(request.getAuditoriumName(), building.getId())) {
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

        validateBuildingAccess(auditorium.getFloor().getBuilding());

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

        validateBuildingAccess(auditorium.getFloor().getBuilding());

        return mapToResponse(auditorium);
    }

    public List<AuditoriumResponse> getAllAuditoriumByBuildingCodeAndFloorNumber(String campusCode, String buildingCode) {

        Building building = buildingRepository.findByCodeAndCampus_Code(buildingCode, campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Building " + buildingCode + " not found in campus " + campusCode));

        validateBuildingAccess(building);

        List<Auditorium> auditoriums = auditoriumRepository.findAllByFloor_Building_CodeAndFloor_Building_Campus_Code(
                buildingCode, campusCode);

        return auditoriums.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public AuditoriumResponse updateAuditorium(String auditoriumCode, AuditoriumUpdateRequest request) {

        Auditorium auditorium = auditoriumRepository.findByAuditoriumCode(auditoriumCode)
                .orElseThrow(() -> new ResourceNotFoundException("Auditorium not found with code: " + auditoriumCode));

        validateBuildingAccess(auditorium.getFloor().getBuilding());

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
    public List<AuditoriumResponse> getAllAuditoriumByCampusCode(String campusCode) {

        Campus campus = campusRepository.findByCode(campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with code: " + campusCode));

        validateCampusAccess(campus);

        return auditoriumRepository.findAllByFloor_Building_Campus_Code(campusCode)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AuditoriumResponse> getAllAuditorium(){

        return auditoriumRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
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

    private void validateCampusAccess(Campus campus) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new AccessDeniedException("Unauthorized access");
        }

        User user = userDetails.getUser();

        if (user.getRole() == Role.SUPER_ADMIN) {
            return;
        }

        if (user.getRole() == Role.CAMPUS_ADMIN && user.getCampus() != null && user.getCampus().getId().equals(campus.getId())) {
            return;
        }
        throw new AccessDeniedException("You do not have permission to access this campus");
    }
}