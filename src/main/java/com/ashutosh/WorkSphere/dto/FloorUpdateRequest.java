package com.ashutosh.WorkSphere.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloorUpdateRequest {

    @NotNull(message = "Floor name is required")
    private String name;
}