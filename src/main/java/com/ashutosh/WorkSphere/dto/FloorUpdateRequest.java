package com.ashutosh.WorkSphere.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloorUpdateRequest {

    @NotBlank(message = "Floor name is required")
    private String name;
}