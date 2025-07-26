package com.example.library_management_be.dto.request;

import com.example.library_management_be.entity.enums.EReservationStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationFilterRequest {
    private String bookTitle;
    private EReservationStatus status;
    private String userEmail;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;
}
