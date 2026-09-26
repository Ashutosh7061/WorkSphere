package com.ashutosh.WorkSphere.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeskBulkCreateResponse {

    private String message;

    private int quantity;

    private String campusName;
    private String campusCode;

    private String buildingName;
    private String buildingCode;

    private Integer floorNumber;

    private String startDeskCode;
    private String endDeskCode;

}