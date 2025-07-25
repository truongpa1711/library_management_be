package com.example.library_management_be.dto.request;

import com.example.library_management_be.entity.enums.EBookCondition;
import com.example.library_management_be.entity.enums.ELoanStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class BookLoanFilterRequest {

    private String email;
    private String fullName;
    private String bookTitle;

    private EBookCondition bookCondition;
    private ELoanStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate; // filter theo borrowDate

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;
}
