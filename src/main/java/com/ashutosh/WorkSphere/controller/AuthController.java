package com.ashutosh.WorkSphere.controller;

import com.ashutosh.WorkSphere.dto.ChangePasswordRequest;
import com.ashutosh.WorkSphere.dto.LoginRequest;
import com.ashutosh.WorkSphere.dto.LoginResponse;
import com.ashutosh.WorkSphere.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // for testing purpose
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser(Authentication authentication) {

        return ResponseEntity.ok("Logged in as: " + authentication.getName()
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication){

        authService.changePassword(authentication.getName(), request);

        return ResponseEntity.ok("Password changed successfully!!");
    }



}
