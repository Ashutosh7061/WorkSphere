package com.ashutosh.WorkSphere.controller;

import com.ashutosh.WorkSphere.dto.department.DepartmentCreateRequest;
import com.ashutosh.WorkSphere.dto.department.DepartmentResponse;
import com.ashutosh.WorkSphere.entity.Department;
import com.ashutosh.WorkSphere.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/campuses/{campusCode}/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN', 'CAMPUS_ADMIN')")
    public ResponseEntity<DepartmentResponse> createDepartment(@PathVariable String campusCode, @Valid @RequestBody DepartmentCreateRequest request){

        DepartmentResponse response = departmentService.createDepartment(campusCode,request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }

}
