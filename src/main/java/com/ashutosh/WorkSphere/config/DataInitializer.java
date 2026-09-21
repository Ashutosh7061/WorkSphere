package com.ashutosh.WorkSphere.config;

import com.ashutosh.WorkSphere.entity.User;
import com.ashutosh.WorkSphere.enums.AccountStatus;
import com.ashutosh.WorkSphere.enums.Role;
import com.ashutosh.WorkSphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String email = "superadmin@worksphere.com";

        if (!userRepository.existsByEmail(email)) {

            User superAdmin = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode("SuperAdmin@123"))
                    .role(Role.SUPER_ADMIN)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            userRepository.save(superAdmin);

            System.out.println("======================================");
            System.out.println("Initial SUPER_ADMIN created");
            System.out.println("Email: " + email);
            System.out.println("Password: SuperAdmin@123");
            System.out.println("======================================");
        }
    }
}