package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.LoginRequest;
import com.ashutosh.WorkSphere.dto.LoginResponse;
import com.ashutosh.WorkSphere.entity.User;
import com.ashutosh.WorkSphere.repository.UserRepository;
import com.ashutosh.WorkSphere.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()
                            )
                    );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow();

            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(userDetails);

            return LoginResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .build();

        } catch (Exception exception) {

            throw exception;
        }
    }
}