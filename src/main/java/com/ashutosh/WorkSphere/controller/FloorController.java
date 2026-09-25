package com.ashutosh.WorkSphere.controller;

import com.ashutosh.WorkSphere.dto.*;
import com.ashutosh.WorkSphere.enums.FloorStatus;
import com.ashutosh.WorkSphere.service.FloorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/floors")
@RequiredArgsConstructor
public class FloorController {

    private final FloorService floorService;

    @PostMapping()
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<FloorResponse> createFloor(@Valid @RequestBody FloorCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(floorService.createFloor(request));
    }


    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<FloorResponse>> getAllFloors() {
        return ResponseEntity.ok(floorService.getAllFloors());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<FloorDetailResponse> getFloorById(@ PathVariable Long id){
        return ResponseEntity.ok(floorService.getFloorById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<FloorUpdateResponse> updateFloor(@PathVariable Long id, @Valid @RequestBody FloorUpdateRequest request) {
        return ResponseEntity.ok(floorService.updateFloor(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<FloorResponse> updateFloorStatus(@PathVariable Long id, @RequestParam FloorStatus status) {
        return ResponseEntity.ok(floorService.updateFloorStatus(id, status));
    }

    @GetMapping("/building/{buildingId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<FloorResponse>> getFloorsByBuildingId(@PathVariable Long buildingId) {
        return ResponseEntity.ok(floorService.getFloorsByBuildingId(buildingId));
    }
}