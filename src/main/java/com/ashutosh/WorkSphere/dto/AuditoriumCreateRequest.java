package com.ashutosh.WorkSphere.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriumCreateRequest {

    @NotBlank(message = "Auditorium name is required")
    @Size(max = 100, message = "Auditorium name must not exceed 100 characters")
    private String auditoriumName;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Floor number is required")
    private Integer floorNumber;

    @NotNull(message = "Building ID is required")
    private Long buildingId;
}