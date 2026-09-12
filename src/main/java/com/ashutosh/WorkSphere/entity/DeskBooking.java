package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "desk_bookings",
        indexes = {
                @Index(name = "idx_desk_booking_date", columnList = "desk_id, booking_date"),
                @Index(name = "idx_employee_booking_date", columnList = "employee_id, booking_date")
        })
public class DeskBooking {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY,optional = false)
        @JoinColumn(name = "desk_id",nullable = false)
        private Desk desk;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "employee_id", nullable = false)
        private Employee employee;

        @Column(name = "booking_date",nullable = false)
        private LocalDate bookingDate;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private BookingStatus status;

        private boolean checkedIn;

        private boolean checkedOut;
}
