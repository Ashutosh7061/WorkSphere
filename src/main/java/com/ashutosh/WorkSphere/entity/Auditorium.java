package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.entity.audit.BaseEntity;
import com.ashutosh.WorkSphere.enums.AuditoriumStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Entity
@Table(
        name = "auditoriums",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames ={"auditorium_code","floor_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class Auditorium extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auditorium_code", nullable = false, unique = true, length = 30)
    private String auditoriumCode;

    @Audited(withModifiedFlag = true)
    @Column(name = "auditorium_name", nullable = false, length = 100)
    private String auditoriumName;

    @Audited(withModifiedFlag = true)
    @Column(nullable = false)
    private Integer capacity;

    @Audited(withModifiedFlag = true)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditoriumStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;
}