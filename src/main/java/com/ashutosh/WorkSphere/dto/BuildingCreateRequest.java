package com.ashutosh.WorkSphere.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingCreateRequest {

    @NotBlank(message = "Building name is required")
    private String name;

    private String address;

    private Long campusId;
}