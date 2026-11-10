package com.ashutosh.WorkSphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(
        auditorAwareRef = "auditorAware",    // same bean name as defines in AuditAware
        modifyOnCreate = false
)
public class WorkSphereApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkSphereApplication.class, args);
    }

}

