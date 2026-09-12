package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.enums.AccountStatus;
import com.ashutosh.WorkSphere.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Employee employee;
}
