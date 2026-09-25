package com.ashutosh.WorkSphere.dto;

import com.ashutosh.WorkSphere.enums.FloorStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloorResponse {

    private Long id;
    private Integer floorNumber;
    private String name;
    private FloorStatus status;
    private Long buildingId;
    private String buildingName;
}