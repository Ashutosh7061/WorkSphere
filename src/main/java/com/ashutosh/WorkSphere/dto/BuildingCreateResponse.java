package com.ashutosh.WorkSphere.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingCreateResponse {
        private BuildingResponse building;
        private String adminEmail;
        private String temporaryPassword;
}
