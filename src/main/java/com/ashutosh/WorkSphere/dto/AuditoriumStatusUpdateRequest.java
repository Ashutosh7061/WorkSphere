package com.ashutosh.WorkSphere.dto;

import com.ashutosh.WorkSphere.enums.AuditoriumStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriumStatusUpdateRequest {

    @NotNull(message = "Auditorium status is required")
    private AuditoriumStatus status;
}
