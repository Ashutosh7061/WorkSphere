package com.ashutosh.WorkSphere.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloorUpdateResponse {

    private Long id;
    private String oldName;
    private String newName;
    private String buildingName;
}