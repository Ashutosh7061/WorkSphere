package com.ashutosh.WorkSphere.controller;

import com.ashutosh.WorkSphere.dto.BuildingCreateRequest;
import com.ashutosh.WorkSphere.dto.BuildingResponse;
import com.ashutosh.WorkSphere.dto.BuildingUpdateRequest;
import com.ashutosh.WorkSphere.enums.BuildingStatus;
import com.ashutosh.WorkSphere.service.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campuses/{campusCode}/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<BuildingResponse> createBuilding(@PathVariable String campusCode, @Valid @RequestBody BuildingCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(buildingService.createBuilding(campusCode,request));
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<BuildingResponse>> getAllBuildingsByCompanyCode(@PathVariable String campusCode) {
        return ResponseEntity.ok(buildingService.getBuildingsByCampusCode(campusCode));
    }


    @GetMapping("/{buildingCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<BuildingResponse> getBuildingByCode(@PathVariable String campusCode, @PathVariable String buildingCode ){
        return ResponseEntity.ok(buildingService.getBuildingByCode(campusCode,buildingCode));
    }

    @PutMapping("/{buildingCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<BuildingResponse> updateBuilding(@PathVariable String campusCode,
                            @PathVariable  String buildingCode, @Valid @RequestBody BuildingUpdateRequest request) {
        return ResponseEntity.ok(buildingService.updateBuilding(campusCode,buildingCode, request));
    }

    @PatchMapping("/{buildingCode}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<BuildingResponse> updateBuildingStatus(@PathVariable String campusCode, @PathVariable String buildingCode, @RequestParam BuildingStatus status) {
        return ResponseEntity.ok(buildingService.updateBuildingStatus(campusCode,buildingCode, status));
    }

}