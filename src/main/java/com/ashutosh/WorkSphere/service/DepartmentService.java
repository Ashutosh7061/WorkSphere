package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.department.DepartmentCreateRequest;
import com.ashutosh.WorkSphere.dto.department.DepartmentResponse;
import com.ashutosh.WorkSphere.entity.Campus;
import com.ashutosh.WorkSphere.entity.Department;
import com.ashutosh.WorkSphere.enums.DepartmentStatus;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.CampusRepository;
import com.ashutosh.WorkSphere.repository.DepartmentRepository;

import com.ashutosh.WorkSphere.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;

import com.ashutosh.WorkSphere.entity.User;
import com.ashutosh.WorkSphere.enums.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CampusRepository campusRepository;

    @Transactional
    public DepartmentResponse createDepartment(String campusCode, DepartmentCreateRequest request) {

        Campus campus = campusRepository.findByCode(campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with code: " + campusCode));

        validateCampusAccess(campus);

        String name = request.getName().trim();
        String departmentCode = request.getDepartmentCode().trim().toUpperCase();

        // Check duplicate department name
        if (departmentRepository.existsByNameIgnoreCaseAndCampus_Id(name, campus.getId())) {
            throw new IllegalArgumentException("Department name already exists in this campus");
        }

        // Check duplicate department code
        if (departmentRepository.existsByDepartmentCodeIgnoreCaseAndCampus_Id(departmentCode, campus.getId())) {
            throw new IllegalArgumentException("Department code already exists in this campus");
        }

        String generatedCode = departmentCode + "-" + campus.getCode();

        // Create department
        Department department = Department.builder()
                .name(name)
                .departmentCode(departmentCode)
                .generatedCode(generatedCode)
                .description(request.getDescription())
                .status(DepartmentStatus.ACTIVE)
                .campus(campus)
                .build();

        Department savedDepartment = departmentRepository.save(department);

        return DepartmentResponse.builder()
                .generatedCode(savedDepartment.getGeneratedCode())
                .name(savedDepartment.getName())
                .departmentCode(savedDepartment.getDepartmentCode())
                .description(savedDepartment.getDescription())
                .status(savedDepartment.getStatus())
                .campusCode(campus.getCode())
                .build();
    }

    private void validateCampusAccess(Campus campus){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User currentUser = userDetails.getUser();

        if(currentUser.getRole() == Role.SUPER_ADMIN){
            return;
        }
        if(currentUser.getRole() == Role.CAMPUS_ADMIN){

            if(currentUser.getCampus() == null || !currentUser.getCampus().getId().equals(campus.getId())){
                throw new AccessDeniedException("You are not authorized to manage this campus");

            }
            return;
        }
        throw new AccessDeniedException("You are not authorized to access departments");
    }
}