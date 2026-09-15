package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.LoginRequest;
import com.ashutosh.WorkSphere.dto.LoginResponse;
import com.ashutosh.WorkSphere.entity.User;
import com.ashutosh.WorkSphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        return LoginResponse.builder()
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
