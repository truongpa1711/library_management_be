package com.example.library_management_be.dto.response;

import com.example.library_management_be.entity.enums.EReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationResponse {
    private String id;
    private String bookId;
    private String bookTitle;
    private String bookAuthor;
    private String userId;
    private String userEmail;
    private LocalDate reservationDate;
    private LocalDate expiryDate;
    private EReservationStatus status;
}
