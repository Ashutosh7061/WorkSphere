package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.CampusCreateRequest;
import com.ashutosh.WorkSphere.dto.CampusResponse;
import com.ashutosh.WorkSphere.entity.Campus;
import com.ashutosh.WorkSphere.entity.Company;
import com.ashutosh.WorkSphere.enums.CampusStatus;
import com.ashutosh.WorkSphere.exception.DuplicateResourceException;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.CampusRepository;
import com.ashutosh.WorkSphere.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampusService {

    private final CampusRepository campusRepository;
    private final CompanyRepository companyRepository;

    public CampusResponse createCampus(CampusCreateRequest request) {

        Company company = companyRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (campusRepository.existsByNameAndCompanyId(request.getName(), company.getId())) {
            throw new DuplicateResourceException("Campus already exists with name: " + request.getName());
        }

        String code = request.getCode().trim().toUpperCase();

        if (campusRepository.existsByCode(code)) {
            throw new DuplicateResourceException(
                    "Campus already exists with code: " + code
            );
        }

        Campus campus = Campus.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .company(company)
                .code(code)
                .build();

        Campus savedCampus = campusRepository.save(campus);

        return mapToResponse(savedCampus);
    }

    public List<CampusResponse> getAllCampuses() {

        return campusRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CampusResponse getCampusById(Long id) {

        Campus campus = campusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with id: " + id));

        return mapToResponse(campus);
    }

    public CampusResponse updateCampus(Long id, CampusCreateRequest request) {

        Campus campus = campusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with id: " + id));

        if (!campus.getName().equals(request.getName()) && campusRepository.existsByNameAndCompanyId(request.getName(), campus.getCompany().getId())) {
            throw new DuplicateResourceException("Campus already exists with name: " + request.getName());
        }

        campus.setName(request.getName());
        campus.setAddress(request.getAddress());
        campus.setCity(request.getCity());
        campus.setState(request.getState());
        campus.setPostalCode(request.getPostalCode());

        Campus updatedCampus = campusRepository.save(campus);

        return mapToResponse(updatedCampus);
    }

    public CampusResponse updateCampusStatus(Long id, CampusStatus status) {

        Campus campus = campusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with id: " + id));

        campus.setStatus(status);

        Campus updatedCampus = campusRepository.save(campus);

        return mapToResponse(updatedCampus);
    }

    private CampusResponse mapToResponse(Campus campus) {

        return CampusResponse.builder()
                .id(campus.getId())
                .name(campus.getName())
                .code(campus.getCode())
                .status(campus.getStatus())
                .address(campus.getAddress())
                .city(campus.getCity())
                .state(campus.getState())
                .postalCode(campus.getPostalCode())
                .companyId(campus.getCompany().getId())
                .build();
    }


}