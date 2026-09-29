package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.enums.RoomStatus;
import com.ashutosh.WorkSphere.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "rooms",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"floor_id", "room_code"})
        }
)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_code", nullable = false, unique = true)
    private String roomCode;

    @Column(nullable = false)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;
}