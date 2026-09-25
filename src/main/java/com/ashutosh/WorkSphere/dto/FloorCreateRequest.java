package com.ashutosh.WorkSphere.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloorCreateRequest {

    @NotNull(message = "Floor number is required")
    private Integer floorNumber;

    private String name;

    @NotNull(message = "Building ID is required")
    private Long buildingId;
}