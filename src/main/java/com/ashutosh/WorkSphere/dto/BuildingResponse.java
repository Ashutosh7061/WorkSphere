package com.ashutosh.WorkSphere.dto;

import com.ashutosh.WorkSphere.enums.BuildingStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingResponse {

    private Long id;
    private String name;
    private String address;
    private BuildingStatus status;
    private Long campusId;
}