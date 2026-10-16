package com.ashutosh.WorkSphere.service;

import com.ashutosh.WorkSphere.dto.LoginRequest;
import com.ashutosh.WorkSphere.dto.LoginResponse;
import com.ashutosh.WorkSphere.entity.User;
import com.ashutosh.WorkSphere.exception.ResourceNotFoundException;
import com.ashutosh.WorkSphere.repository.UserRepository;
import com.ashutosh.WorkSphere.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ashutosh.WorkSphere.dto.ChangePasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(()-> new ResourceNotFoundException("Email not found"));

            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();

            String token;
            if (user.isPasswordChangeRequired()) {
                token = jwtService.generatePasswordChangeToken(userDetails);
            } else {
                token = jwtService.generateToken(userDetails);
            }

            return LoginResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .passwordChangeRequired(user.isPasswordChangeRequired())
                    .build();

        } catch (Exception exception) {

            throw exception;
        }
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request){

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Current Password is incorrect");
        }

        if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST," New Password must differ fron the Current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        if(user.isPasswordChangeRequired()){
            user.setPasswordChangeRequired(false);
        }

        userRepository.save(user);
    }
}