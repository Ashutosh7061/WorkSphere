
package com.ashutosh.WorkSphere.controller;

import com.ashutosh.WorkSphere.dto.BuildingResponse;
import com.ashutosh.WorkSphere.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class CompanyBasedController {

    private final BuildingService buildingService;

    @GetMapping("/buildings")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<BuildingResponse>> getAllBuildings() {
        return ResponseEntity.ok(buildingService.getAllBuildings());
    }
}
