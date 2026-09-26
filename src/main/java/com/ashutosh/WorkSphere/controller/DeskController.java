package com.ashutosh.WorkSphere.controller;

import com.ashutosh.WorkSphere.dto.DeskBulkCreateResponse;
import com.ashutosh.WorkSphere.dto.DeskCreateRequest;
import com.ashutosh.WorkSphere.dto.DeskResponse;
import com.ashutosh.WorkSphere.dto.DeskStatusUpdateRequest;
import com.ashutosh.WorkSphere.service.DeskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/desks")
@RequiredArgsConstructor
public class DeskController {

    private final DeskService deskService;

    @PostMapping("/buildings/{buildingId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<DeskBulkCreateResponse> createDesks(@PathVariable Long buildingId, @Valid @RequestBody DeskCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(deskService.createDesks(buildingId, request));
    }

    @GetMapping("/buildings/{buildingId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<DeskResponse>> getDesksByBuildingAndFloor(@PathVariable Long buildingId, @RequestParam Integer floorNumber) {
        return ResponseEntity.ok(deskService.getDesksByBuildingAndFloor(buildingId, floorNumber));
    }

    @GetMapping("/code/{deskCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<DeskResponse> getDeskByCode(@PathVariable String deskCode) {
        return ResponseEntity.ok(deskService.getDeskByCode(deskCode));
    }

    @PatchMapping("/updateStatus")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> updateDeskStatus(@Valid @RequestBody DeskStatusUpdateRequest request) {
        return ResponseEntity.ok(deskService.updateDeskStatus(request));
    }
}
