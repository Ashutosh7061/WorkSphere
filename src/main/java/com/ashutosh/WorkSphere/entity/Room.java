package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.entity.audit.BaseEntity;
import com.ashutosh.WorkSphere.enums.RoomStatus;
import com.ashutosh.WorkSphere.enums.RoomType;
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
        name = "rooms",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"floor_id", "room_code"})
        }
)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_code", nullable = false, unique = true)
    private String roomCode;

    @Audited(withModifiedFlag = true)
    @Column(nullable = false)
    private String roomName;

    @Audited(withModifiedFlag = true)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Audited(withModifiedFlag = true)
    private Integer capacity;

    @Audited(withModifiedFlag = true)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;
}