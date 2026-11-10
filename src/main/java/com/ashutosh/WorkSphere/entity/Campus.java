package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.entity.audit.BaseEntity;
import com.ashutosh.WorkSphere.enums.CampusStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@Table(
        name = "campuses",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "company_id"})
        }
)
public class Campus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Audited(withModifiedFlag = true)
    @Column(nullable = false)
    private String name;

    @Audited(withModifiedFlag = true)
    private String address;

    @Audited(withModifiedFlag = true)
    @Column(nullable = false)
    private String city;

    @Audited(withModifiedFlag = true)
    private String state;

    @Audited(withModifiedFlag = true)
    private String postalCode;

    @Audited(withModifiedFlag = true)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CampusStatus status = CampusStatus.ACTIVE;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Company company;
}