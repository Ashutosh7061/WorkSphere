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

    @PostMapping("/campuses/{campusCode}/buildings/{buildingCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AuditoriumResponse> createAuditorium(@PathVariable String campusCode, @PathVariable String buildingCode, @Valid @RequestBody AuditoriumCreateRequest request) {

        AuditoriumResponse response = auditoriumService.createAuditorium(campusCode, buildingCode, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{auditoriumCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AuditoriumResponse> getAuditoriumByCode(@PathVariable String auditoriumCode) {
        return ResponseEntity.ok(auditoriumService.getAuditoriumByCode(auditoriumCode));
    }

    @GetMapping("/campuses/{campusCode}/buildings/{buildingCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<AuditoriumResponse>> getAuditoriumsByBuildingCode(@PathVariable String campusCode,@PathVariable String buildingCode) {
        return ResponseEntity.ok(auditoriumService.getAllAuditoriumByBuildingCodeAndFloorNumber(campusCode,buildingCode));
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

    @GetMapping("/campuses/{campusCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<AuditoriumResponse>> getAllAuditoriumByCampus(@PathVariable String campusCode){
        List<AuditoriumResponse> responses =auditoriumService.getAllAuditoriumByCampusCode(campusCode);

        return ResponseEntity.ok(responses);
    }
}