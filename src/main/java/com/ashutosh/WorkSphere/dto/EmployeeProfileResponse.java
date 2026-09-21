package com.ashutosh.WorkSphere.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeProfileResponse {

    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private boolean profileCompleted;
}
