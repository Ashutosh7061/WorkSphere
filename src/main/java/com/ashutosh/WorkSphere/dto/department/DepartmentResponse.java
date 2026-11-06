package com.ashutosh.WorkSphere.dto.department;

import com.ashutosh.WorkSphere.enums.DepartmentStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {

    private String generatedCode;
    private String name;
    private String departmentCode;
    private String description;
    private DepartmentStatus status;
    private String campusCode;
}