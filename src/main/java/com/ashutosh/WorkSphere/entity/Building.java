package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.enums.BuildingStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "buildings",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "campus_id"})
        }
)public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BuildingStatus status = BuildingStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;
}
