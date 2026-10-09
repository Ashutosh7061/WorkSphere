package com.ashutosh.WorkSphere.controller;

import com.ashutosh.WorkSphere.dto.CampusCreateRequest;
import com.ashutosh.WorkSphere.dto.CampusResponse;
import com.ashutosh.WorkSphere.dto.CampusUpdateRequest;
import com.ashutosh.WorkSphere.enums.CampusStatus;
import com.ashutosh.WorkSphere.service.CampusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campuses")
@RequiredArgsConstructor
public class CampusController {

    private final CampusService campusService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CampusResponse> createCampus(@Valid @RequestBody CampusCreateRequest request) {

        CampusResponse response = campusService.createCampus(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<CampusResponse>> getAllCampuses() {
        return ResponseEntity.ok(campusService.getAllCampuses());
    }

    @GetMapping("/{campusCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CampusResponse> getCampusByCode(@PathVariable String campusCode) {
        return ResponseEntity.ok(campusService.getCampusByCode(campusCode));
    }

    @PutMapping("/{campusCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CampusResponse> updateCampus(@PathVariable String campusCode, @Valid @RequestBody CampusUpdateRequest request) {

        return ResponseEntity.ok(campusService.updateCampus(campusCode, request));
    }

    @PatchMapping("/{campusCode}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CampusResponse> updateCampusStatus(@PathVariable String campusCode, @RequestParam CampusStatus status) {

        return ResponseEntity.ok(campusService.updateCampusStatus(campusCode, status));
    }
}