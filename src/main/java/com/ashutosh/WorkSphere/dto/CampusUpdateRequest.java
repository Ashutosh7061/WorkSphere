package com.ashutosh.WorkSphere.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampusUpdateRequest {

    @NotBlank(message = "Name should not be blank")
    private String name;

    private String address;

    @NotBlank(message = "City should not be blank")
    private String city;

    private String state;

    private String postalCode;

}
