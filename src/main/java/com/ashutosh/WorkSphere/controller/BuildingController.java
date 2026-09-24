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
@RequestMapping("/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<BuildingResponse> createBuilding(@Valid @RequestBody BuildingCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(buildingService.createBuilding(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<BuildingResponse>> getAllBuildings() {
        return ResponseEntity.ok(buildingService.getAllBuildings());
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<BuildingResponse> getBuildingById(@PathVariable Long id) {
        return ResponseEntity.ok(buildingService.getBuildingById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<BuildingResponse> updateBuilding(@PathVariable Long id, @Valid @RequestBody BuildingUpdateRequest request) {

        return ResponseEntity.ok(buildingService.updateBuilding(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<BuildingResponse> updateBuildingStatus(@PathVariable Long id, @RequestParam BuildingStatus status) {

        return ResponseEntity.ok(buildingService.updateBuildingStatus(id, status));
    }
}