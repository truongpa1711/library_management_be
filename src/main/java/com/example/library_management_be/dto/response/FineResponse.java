package com.example.library_management_be.dto.response;

import com.example.library_management_be.entity.enums.EFineReason;
import com.example.library_management_be.entity.enums.EFineStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class FineResponse {
    private Long id;
    private BigDecimal amount;
    private EFineReason reason;
    private EFineStatus status;
    private LocalDateTime issuedDate;
    private LocalDateTime paidDate;

    private String userFullName;
    private String userEmail;
    private Long bookLoanId;
}
