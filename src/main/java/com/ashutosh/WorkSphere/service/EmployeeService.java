package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.EmployeeProfileRequest;
import com.ashutosh.WorkSphere.entity.Employee;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee getEmployeeByEmail(String email){
        return employeeRepository.findByUserEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Employee not found"));
    }

    public Employee completeProfile(String email, EmployeeProfileRequest request){

        Employee employee = employeeRepository.findByUserEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Employee profile not found"));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setPhoneNo(request.getPhoneNo());

        employee.setProfileCompleted(true);

        return employeeRepository.save(employee);
    }
}
