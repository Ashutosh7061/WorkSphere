package com.ashutosh.WorkSphere.controller;

import com.ashutosh.WorkSphere.dto.CompanyCreateRequest;
import com.ashutosh.WorkSphere.dto.CompanyResponse;
import com.ashutosh.WorkSphere.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
            @Valid @RequestBody CompanyCreateRequest request) {

        CompanyResponse response = companyService.createCompany(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}