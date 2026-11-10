package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.entity.audit.BaseEntity;
import com.ashutosh.WorkSphere.enums.DepartmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@Table(name = "departments",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "campus_id"}),
                @UniqueConstraint(columnNames = {"department_code", "campus_id"}),
                @UniqueConstraint(columnNames = {"generated_code"})})
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Audited(withModifiedFlag = true)
    @Column(nullable = false)
    private String name;

    @Column(name = "department_code", nullable = false, length = 20)
    private String departmentCode;

    @Column(name = "generated_code", nullable = false, unique = true, length = 40)
    private String generatedCode;

    @Audited(withModifiedFlag = true)
    private String description;

    @Audited(withModifiedFlag = true)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;
}