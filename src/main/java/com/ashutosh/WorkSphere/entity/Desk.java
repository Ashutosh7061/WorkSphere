package com.ashutosh.WorkSphere.entity;


import com.ashutosh.WorkSphere.enums.DeskStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "desks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"floor_id", "desk_code"})
        })

public class Desk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "desk_code", nullable = false, unique = true)
    private String deskCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeskStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;
}
