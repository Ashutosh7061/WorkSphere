package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.RoomBooking;
import com.ashutosh.WorkSphere.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface RoomBookingRepository
        extends JpaRepository<RoomBooking, Long> {

    @Query("""
                SELECT COUNT(r) > 0
                FROM RoomBooking r
                WHERE r.meetingRoom.id = :roomId
                AND r.status = :status
                AND r.startTime < :endTime
                AND r.endTime > :startTime
            """)
    boolean existsOverlappingBooking(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") BookingStatus status
    );
}