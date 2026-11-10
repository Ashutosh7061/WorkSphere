package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.entity.audit.BaseEntity;
import com.ashutosh.WorkSphere.enums.BuildingStatus;
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
@Table(
        name = "buildings",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "campus_id"}),
                @UniqueConstraint(columnNames = {"code", "campus_id"})
        }
)

public class Building extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Audited(withModifiedFlag = true)
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 10)
    private String code;

    @Audited(withModifiedFlag = true)
    private String address;

    @Audited(withModifiedFlag = true)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BuildingStatus status = BuildingStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;
}
