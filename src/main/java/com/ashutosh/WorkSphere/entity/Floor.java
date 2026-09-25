package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.enums.FloorStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "floors",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"building_id", "floor_number"})
        })
public class Floor {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private Integer floorNumber;

        private String name;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        @Builder.Default
        private FloorStatus status = FloorStatus.ACTIVE;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "building_id", nullable = false)
        private Building building;
}
