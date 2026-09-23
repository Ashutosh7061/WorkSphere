package com.ashutosh.WorkSphere.dto;

import com.ashutosh.WorkSphere.enums.CampusStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampusResponse {

    private Long id;
    private String name;
    private CampusStatus status;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private Long companyId;
}