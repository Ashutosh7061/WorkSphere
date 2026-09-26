package com.ashutosh.WorkSphere.dto;

import com.ashutosh.WorkSphere.enums.DeskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeskStatusUpdateRequest {

    @NotBlank(message = "Desk prefix is required")
    private String deskPrefix;

    @NotNull(message = "Start desk number is required")
    @Min(value = 1, message = "Start desk number must be positive")
    private Integer startDeskNumber;

    @NotNull(message = "End desk number is required")
    @Min(value = 1, message = "End desk number must be positive")
    private Integer endDeskNumber;

    @NotNull(message = "Status is required")
    private DeskStatus status;
}