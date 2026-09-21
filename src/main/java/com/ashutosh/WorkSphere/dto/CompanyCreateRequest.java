package com.ashutosh.WorkSphere.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyCreateRequest {

    @NotBlank(message = "Company name is required")
    private String name;

    @Email(message = "Invalid company email")
    private String email;

    private String phone;

    private String address;
}