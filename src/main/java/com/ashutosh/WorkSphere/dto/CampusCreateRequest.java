package com.ashutosh.WorkSphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampusCreateRequest {

    @NotBlank(message = "Campus name is required")
    private String name;

    private String address;

    @NotBlank(message = "City is required")
    private String city;

    private String state;

    @Pattern(
            regexp = "^[0-9]{6}$",
            message = "Postal code must contain exactly 6 digits"
    )
    private String postalCode;
}