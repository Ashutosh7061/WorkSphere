package com.ashutosh.WorkSphere.dto;

import com.ashutosh.WorkSphere.enums.AuditoriumStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriumResponse {

    private Long id;
    private String auditoriumCode;
    private String auditoriumName;
    private Integer capacity;
    private AuditoriumStatus status;
    private Integer floorNumber;
    private String buildingName;
    private String buildingCode;
    private String campusName;
    private String campusCode;
}