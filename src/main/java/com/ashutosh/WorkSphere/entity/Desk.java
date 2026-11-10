package com.ashutosh.WorkSphere.entity;


import com.ashutosh.WorkSphere.entity.audit.BaseEntity;
import com.ashutosh.WorkSphere.enums.DeskStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Audited
@Table(name = "desks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"floor_id", "desk_code"})
        })

public class Desk extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "desk_code", nullable = false, unique = true)
    private String deskCode;

    @Audited(withModifiedFlag = true)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeskStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;
}
