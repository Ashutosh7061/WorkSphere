package com.ashutosh.WorkSphere.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampusCreationResponse {

    private CampusResponse campus;
    private String adminEmail;
    private String temporaryPassword;
}