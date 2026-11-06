package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.enums.DepartmentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "departments",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "campus_id"}),
                @UniqueConstraint(columnNames = {"department_code", "campus_id"}),
                @UniqueConstraint(columnNames = {"generated_code"})})
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "department_code", nullable = false, length = 20)
    private String departmentCode;

    @Column(name = "generated_code", nullable = false, unique = true, length = 40)
    private String generatedCode;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;
}