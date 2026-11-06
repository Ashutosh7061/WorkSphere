package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.BuildingCreateRequest;
import com.ashutosh.WorkSphere.dto.BuildingCreateResponse;
import com.ashutosh.WorkSphere.dto.BuildingResponse;
import com.ashutosh.WorkSphere.dto.BuildingUpdateRequest;
import com.ashutosh.WorkSphere.entity.Building;
import com.ashutosh.WorkSphere.entity.Campus;
import com.ashutosh.WorkSphere.entity.User;
import com.ashutosh.WorkSphere.enums.AccountStatus;
import com.ashutosh.WorkSphere.enums.BuildingStatus;
import com.ashutosh.WorkSphere.enums.CampusStatus;
import com.ashutosh.WorkSphere.enums.Role;
import com.ashutosh.WorkSphere.exception.DuplicateResourceException;
import com.ashutosh.WorkSphere.exception.InactiveResourceException;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.BuildingRepository;
import com.ashutosh.WorkSphere.repository.CampusRepository;
import com.ashutosh.WorkSphere.repository.UserRepository;
import com.ashutosh.WorkSphere.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final CampusRepository campusRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private  static  final SecureRandom SECURE_RANDOM = new SecureRandom();


    @Transactional
    public BuildingCreateResponse createBuilding(String campusCode, BuildingCreateRequest request) {

        // to check whether the campus admin have permission do to the task
        validateCampusAccess(campusCode);

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

        String adminEmail = ("campus." + campus.getCode() + "." + code + "@worksphere.com").toLowerCase();

        if (userRepository.existsByEmail(adminEmail)) {
            throw new DuplicateResourceException("User already exists with email: " + adminEmail);
        }

        Building building = Building.builder()
                .name(request.getName())
                .address(request.getAddress())
                .campus(campus)
                .status(BuildingStatus.ACTIVE)
                .code(code)
                .build();

        Building savedBuilding = buildingRepository.save(building);

        String temporaryPassword = generateTemporaryPassword();

        // Create building admin
        User buildingAdmin = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode(temporaryPassword))
                .role(Role.BUILDING_ADMIN)
                .accountStatus(AccountStatus.ACTIVE)
                .passwordChangeRequired(true)
                .campus(savedBuilding.getCampus())
                .building(savedBuilding)
                .build();

        userRepository.save(buildingAdmin);

        return BuildingCreateResponse.builder()
                .building(mapToResponse(savedBuilding))
                .adminEmail(adminEmail)
                .temporaryPassword(temporaryPassword)
                .build();
    }

    public List<BuildingResponse> getAllBuildings() {

        return buildingRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public BuildingResponse getBuildingByCode(String campusCode, String buildingCode) {
        validateCampusAccess(campusCode);

        Building building = buildingRepository.findByCodeAndCampus_Code(buildingCode, campusCode).orElseThrow(() -> new ResourceNotFoundException("Building not found with code: " + buildingCode + " in campus: " + campusCode));

        return mapToResponse(building);
    }

    public BuildingResponse updateBuilding(String campusCode, String buildingCode, BuildingUpdateRequest request) {

        validateCampusAccess(campusCode);

        Building building = buildingRepository.findByCodeAndCampus_Code(buildingCode, campusCode).orElseThrow(() -> new ResourceNotFoundException("Building not found with code: " + buildingCode + " in campus: " + campusCode));

        if (!building.getName().equalsIgnoreCase(request.getName()) && buildingRepository.existsByNameAndCampus_Code(request.getName(), campusCode)) {

            throw new DuplicateResourceException("Building already exists with name: " + request.getName());
        }
        building.setName(request.getName());
        building.setAddress(request.getAddress());

        Building updatedBuilding = buildingRepository.save(building);

        return mapToResponse(updatedBuilding);

    }

    public BuildingResponse updateBuildingStatus(String campusCode, String buildingCode, BuildingStatus status) {

        validateCampusAccess(campusCode);

        Building building = buildingRepository.findByCodeAndCampus_Code(buildingCode, campusCode).orElseThrow(() -> new ResourceNotFoundException("Building not found with code: " + buildingCode + " in campus: " + campusCode));

        building.setStatus(status);
        Building updatedBuilding = buildingRepository.save(building);

        return mapToResponse(updatedBuilding);
    }

    public List<BuildingResponse> getBuildingsByCampusCode(String campusCode) {

        validateCampusAccess(campusCode);

        Campus campus = campusRepository.findByCode(campusCode).orElseThrow(() -> new ResourceNotFoundException("Campus not found with code: " + campusCode));

        return buildingRepository.findAllByCampusId(campus.getId()).stream().map(this::mapToResponse).toList();
    }

    private BuildingResponse mapToResponse(Building building) {
        return BuildingResponse.builder().id(building.getId()).name(building.getName()).code(building.getCode()).address(building.getAddress()).status(building.getStatus()).campusId(building.getCampus().getId()).build();
    }



    private void validateCampusAccess(String campusCode) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        User user = userDetails.getUser();

        // SUPER_ADMIN has global access.
        if (user.getRole() == Role.SUPER_ADMIN) {
            return;
        }
        if (user.getRole() == Role.CAMPUS_ADMIN) {
            if (user.getCampus() == null) {
                throw new AccessDeniedException("No campus assigned to this user");
            }
            Campus requestedCampus = campusRepository.findByCode(campusCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Campus not found with code: " + campusCode));

            if (user.getCampus().getId().equals(requestedCampus.getId())) {
                return;
            }
        }

        throw new AccessDeniedException("You are not authorized to access campus: " + campusCode);
    }


    private String generateTemporaryPassword() {
        byte[] randomBytes = new byte[18];
        SECURE_RANDOM.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

}