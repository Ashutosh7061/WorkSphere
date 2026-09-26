package com.ashutosh.WorkSphere.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeskCreateRequest {

    @NotNull(message = "Floor number is required")
    private Integer floorNumber;

    @NotNull(message = "Number of desks is required")
    @Min(value = 1, message = "Number of desks must be at least 1")
    private Integer quantity;
}