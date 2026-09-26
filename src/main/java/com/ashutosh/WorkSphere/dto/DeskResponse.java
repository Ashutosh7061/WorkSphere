package com.ashutosh.WorkSphere.dto;

import com.ashutosh.WorkSphere.enums.DeskStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeskResponse {

    private Long id;
    private String deskCode;
    private DeskStatus status;
    private Long floorId;
    private Integer floorNumber;
    private String buildingName;
    private String buildingCode;
    private String campusName;
    private String campusCode;
}