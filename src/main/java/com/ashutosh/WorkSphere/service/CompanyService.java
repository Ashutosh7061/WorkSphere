package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.CompanyCreateRequest;
import com.ashutosh.WorkSphere.dto.CompanyResponse;
import com.ashutosh.WorkSphere.entity.Company;
import com.ashutosh.WorkSphere.exception.DuplicateResourceException;
import com.ashutosh.WorkSphere.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyResponse createCompany(CompanyCreateRequest request) {

        if (companyRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Company already exists with name: " + request.getName()
            );
        }

        Company company = Company.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        Company savedCompany = companyRepository.save(company);

        return mapToResponse(savedCompany);
    }

    private CompanyResponse mapToResponse(Company company) {

        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .email(company.getEmail())
                .phone(company.getPhone())
                .address(company.getAddress())
                .build();
    }
}