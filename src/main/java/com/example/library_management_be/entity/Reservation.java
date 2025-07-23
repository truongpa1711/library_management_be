package com.example.library_management_be.entity;

import com.example.library_management_be.entity.enums.EReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Reservation extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; // Sách được đặt tract

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người dùng đã đặt sách

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate; // Ngày đặt sách


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EReservationStatus status = EReservationStatus.PENDING;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;
}
