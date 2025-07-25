package com.example.library_management_be.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class BookLoanRequest {
    @NotNull(message = "Book ID cannot be null or empty")
    private List<Long> bookIds;

    @NotNull(message = "dueDate cannot be null or empty")
    private LocalDate dueDate;
}
