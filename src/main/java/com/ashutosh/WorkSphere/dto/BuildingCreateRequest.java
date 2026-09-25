package com.ashutosh.WorkSphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingCreateRequest {

    @NotBlank(message = "Building name is required")
    private String name;

    @NotBlank(message = "Building code is required")
    @Size(max = 10, message = "Building code must not exceed 10 characters")
    private String code;

    private String address;

    private Long campusId;
}