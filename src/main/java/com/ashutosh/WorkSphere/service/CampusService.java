package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.CampusCreateRequest;
import com.ashutosh.WorkSphere.dto.CampusCreationResponse;
import com.ashutosh.WorkSphere.dto.CampusResponse;
import com.ashutosh.WorkSphere.dto.CampusUpdateRequest;
import com.ashutosh.WorkSphere.entity.Campus;
import com.ashutosh.WorkSphere.entity.Company;
import com.ashutosh.WorkSphere.entity.User;
import com.ashutosh.WorkSphere.enums.AccountStatus;
import com.ashutosh.WorkSphere.enums.CampusStatus;
import com.ashutosh.WorkSphere.enums.Role;
import com.ashutosh.WorkSphere.exception.DuplicateResourceException;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.CampusRepository;
import com.ashutosh.WorkSphere.repository.CompanyRepository;
import com.ashutosh.WorkSphere.repository.UserRepository;
import com.ashutosh.WorkSphere.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampusService {

    private final CampusRepository campusRepository;
    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Transactional
    public CampusCreationResponse createCampus(CampusCreateRequest request) {

        Company company = companyRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        String campusCode = request.getCode().trim().toUpperCase();
        String campusAdminEmail = request.getAdminEmail().trim().toLowerCase();

        if(campusRepository.existsByCode(request.getCode())){
            throw new DuplicateResourceException("Campus already exists with code: " + request.getCode());
        }

        if (campusRepository.existsByNameAndCompanyId(request.getName(), company.getId())) {
            throw new DuplicateResourceException("Campus already exists with name: " + request.getName());
        }

        if (campusRepository.existsByCode(campusCode)) {
            throw new DuplicateResourceException("Campus already exists with code: " + campusCode);
        }

        if(userRepository.existsByEmail(campusAdminEmail)){
            throw new DuplicateResourceException("User already exists with email: "+ campusAdminEmail);
        }

        Campus campus = Campus.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .company(company)
                .code(campusCode)
                .build();

        Campus savedCampus = campusRepository.save(campus);

        String temporaryPassword = generateTemporaryPassword();

        User campusAdmin = User.builder()
                .email(campusAdminEmail)
                .password(passwordEncoder.encode(temporaryPassword))
                .role(Role.CAMPUS_ADMIN)
                .accountStatus(AccountStatus.ACTIVE)
                .passwordChangeRequired(true)
                .campus(savedCampus)
                .build();

        userRepository.save(campusAdmin);

        return CampusCreationResponse.builder()
                .campus(mapToResponse(savedCampus))
                .adminEmail(campusAdminEmail)
                .temporaryPassword(temporaryPassword)
                .build();
    }


    public List<CampusResponse> getAllCampuses() {

        return campusRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CampusResponse getCampusByCode(String campusCode) {

        Campus campus = campusRepository.findByCode(campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with code: " + campusCode));

        validateCampusAccess(campus);

        return mapToResponse(campus);
    }


    public CampusResponse updateCampus(String campusCode, CampusUpdateRequest request) {

        Campus campus = campusRepository.findByCode(campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with code: " + campusCode));

        validateCampusAccess(campus);

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

    public CampusResponse updateCampusStatus(String campusCode, CampusStatus status) {

        Campus campus = campusRepository.findByCode(campusCode)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with code: " + campusCode));

        validateCampusAccess(campus);
        
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

    private String generateTemporaryPassword() {
        byte[] randomBytes = new byte[18];
        SECURE_RANDOM.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    private void validateCampusAccess(Campus campus) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User currentUser = userDetails.getUser();

        if (currentUser.getRole() == Role.SUPER_ADMIN) {
            return;
        }

        if (currentUser.getRole() == Role.CAMPUS_ADMIN) {
            if (currentUser.getCampus() == null || !currentUser.getCampus().getId().equals(campus.getId())) {
                throw new AccessDeniedException("You are not authorized to access this campus");
            }
            return;
        }
        throw new AccessDeniedException("You are not authorized to access campus details");
    }
}