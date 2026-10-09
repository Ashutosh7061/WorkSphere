package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.enums.AuditoriumStatus;
import jakarta.persistence.*;
import lombok.*;

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
public class Auditorium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auditorium_code", nullable = false, unique = true, length = 30)
    private String auditoriumCode;

    @Column(name = "auditorium_name", nullable = false, length = 100)
    private String auditoriumName;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditoriumStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;
}