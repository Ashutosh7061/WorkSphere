
package com.ashutosh.WorkSphere.controller;

import com.ashutosh.WorkSphere.dto.AuditoriumResponse;
import com.ashutosh.WorkSphere.dto.BuildingResponse;
import com.ashutosh.WorkSphere.dto.FloorResponse;
import com.ashutosh.WorkSphere.service.AuditoriumService;
import com.ashutosh.WorkSphere.service.BuildingService;
import com.ashutosh.WorkSphere.service.FloorService;
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
    private final FloorService floorService;
    private final AuditoriumService auditoriumService;

    @GetMapping("/buildings")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<BuildingResponse>> getAllBuildings() {
        return ResponseEntity.ok(buildingService.getAllBuildings());
    }

    @GetMapping("/floors")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<FloorResponse>> getAllFloors() {
        return ResponseEntity.ok(floorService.getAllFloors());
    }

    @GetMapping("/campuses/{campusCode}/floors")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<FloorResponse>> getFloorsByCampusCode(@PathVariable String campusCode) {

        return ResponseEntity.ok(floorService.getFloorsByCampusCode(campusCode));
    }

    @GetMapping("/auditoriums")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<AuditoriumResponse>> getAllAuditorium(){
        return ResponseEntity.ok( auditoriumService.getAllAuditorium());
    }
}
