package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.entity.audit.BaseEntity;
import com.ashutosh.WorkSphere.enums.FloorStatus;
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
@Table(name = "floors",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"building_id", "floor_number"})
        })
public class Floor extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private Integer floorNumber;

        @Audited(withModifiedFlag = true)
        private String name;

        @Audited(withModifiedFlag = true)
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        @Builder.Default
        private FloorStatus status = FloorStatus.ACTIVE;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "building_id", nullable = false)
        private Building building;
}
