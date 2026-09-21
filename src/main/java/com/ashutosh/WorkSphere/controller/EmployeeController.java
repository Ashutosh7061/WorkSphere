package com.ashutosh.WorkSphere.controller;

import com.ashutosh.WorkSphere.dto.EmployeeProfileRequest;
import com.ashutosh.WorkSphere.dto.EmployeeProfileResponse;
import com.ashutosh.WorkSphere.entity.Employee;
import com.ashutosh.WorkSphere.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PutMapping("/profile")
    public ResponseEntity<EmployeeProfileResponse> completeProfile(
            @Valid @RequestBody EmployeeProfileRequest request, Authentication authentication) {

        System.out.println("PROFILE CONTROLLER CALLED");


        Employee employee = employeeService.completeProfile(authentication.getName(), request);

        EmployeeProfileResponse response = new EmployeeProfileResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getPhoneNo(),
                employee.isProfileCompleted()
        );

        return ResponseEntity.ok(response);
    }
}
