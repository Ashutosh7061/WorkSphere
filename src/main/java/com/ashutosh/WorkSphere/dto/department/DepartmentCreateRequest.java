package com.ashutosh.WorkSphere.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentCreateRequest {

    @NotBlank(message = "Department name is required")
    @Size(max = 100, message = "Department name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Department code is required")
    @Size(max = 20, message = "Department code cannot exceed 20 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9]+$",
            message = "Department code can contain only letters and numbers"
    )
    private String departmentCode;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}