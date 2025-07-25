package com.example.library_management_be.dto.response;

import com.example.library_management_be.entity.enums.EBookCondition;
import com.example.library_management_be.entity.enums.ELoanStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class GetAllBookLoanResponse {
    private Long loanId;
    private String userFullName;
    private String userEmail;
    private String bookTitle;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private EBookCondition bookCondition;
    private ELoanStatus status;
}
