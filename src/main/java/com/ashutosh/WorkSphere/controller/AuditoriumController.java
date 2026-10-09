package com.ashutosh.WorkSphere.controller;

import com.ashutosh.WorkSphere.dto.AuditoriumCreateRequest;
import com.ashutosh.WorkSphere.dto.AuditoriumResponse;
import com.ashutosh.WorkSphere.dto.AuditoriumStatusUpdateRequest;
import com.ashutosh.WorkSphere.dto.AuditoriumUpdateRequest;
import com.ashutosh.WorkSphere.service.AuditoriumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditoriums")
@RequiredArgsConstructor
public class AuditoriumController {

    private final AuditoriumService auditoriumService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AuditoriumResponse> createAuditorium(@Valid @RequestBody AuditoriumCreateRequest request) {

        AuditoriumResponse response = auditoriumService.createAuditorium(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{auditoriumCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AuditoriumResponse> getAuditoriumByCode(@PathVariable String auditoriumCode) {
        return ResponseEntity.ok(auditoriumService.getAuditoriumByCode(auditoriumCode));
    }

    @GetMapping("/building/{buildingCode}/floor/{floorNumber}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<AuditoriumResponse>> getAuditoriumsByBuildingCodeAndFloorNumber(@PathVariable String buildingCode, @PathVariable Integer floorNumber) {
        return ResponseEntity.ok(auditoriumService.getAllAuditoriumByBuildingCodeAndFloorNumber(buildingCode, floorNumber));
    }

    @PutMapping("/{auditoriumCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AuditoriumResponse> updateAuditorium(@PathVariable String auditoriumCode, @Valid @RequestBody AuditoriumUpdateRequest request) {

        AuditoriumResponse response = auditoriumService.updateAuditorium(auditoriumCode, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{auditoriumCode}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AuditoriumResponse> updateAuditoriumStatus(@PathVariable String auditoriumCode, @Valid @RequestBody AuditoriumStatusUpdateRequest request) {

        AuditoriumResponse response = auditoriumService.updateAuditoriumStatus(auditoriumCode, request);

        return ResponseEntity.ok(response);
    }
}