package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Desk;
import com.ashutosh.WorkSphere.entity.DeskBooking;
import com.ashutosh.WorkSphere.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DeskBookingRepository extends JpaRepository<DeskBooking, Long> {

    Optional<DeskBooking> findByDeskIdAndBookingDateAndStatus(
            Long deskId,
            LocalDate bookingDate,
            BookingStatus status
    );
}
