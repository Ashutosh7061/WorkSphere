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
@RequestMapping("/campuses/{campusCode}/buildings/{buildingCode}/floors")
@RequiredArgsConstructor
public class FloorController {

    private final FloorService floorService;

    @PostMapping()
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<FloorResponse> createFloor( @PathVariable String campusCode, @PathVariable String buildingCode,
                                                      @Valid @RequestBody FloorCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(floorService.createFloor(campusCode, buildingCode, request));
    }


//    @GetMapping
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
//    public ResponseEntity<List<FloorResponse>> getAllFloors() {
//        return ResponseEntity.ok(floorService.getAllFloors());
//    }

    @GetMapping("/{floorNumber}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<FloorDetailResponse> getFloorById(  @PathVariable String campusCode, @PathVariable String buildingCode,
                                                              @ PathVariable Integer floorNumber){
        return ResponseEntity.ok(floorService.getFloorById( campusCode, buildingCode, floorNumber));
    }

    @PutMapping("/{floorNumber}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<FloorUpdateResponse> updateFloor( @PathVariable String campusCode, @PathVariable String buildingCode,
                                                            @PathVariable Integer floorNumber, @Valid @RequestBody FloorUpdateRequest request) {
        return ResponseEntity.ok(floorService.updateFloor(campusCode, buildingCode,floorNumber, request));
    }

    @PatchMapping("/{floorNumber}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<FloorResponse> updateFloorStatus( @PathVariable String campusCode, @PathVariable String buildingCode,
                                                            @PathVariable Integer floorNumber, @RequestParam FloorStatus status) {
        return ResponseEntity.ok(floorService.updateFloorStatus(campusCode,buildingCode,floorNumber, status));
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<FloorResponse>> getFloorsByBuildingId( @PathVariable String campusCode, @PathVariable String buildingCode) {
        return ResponseEntity.ok(floorService.getFloorsByBuildingId(campusCode,buildingCode));
    }
}