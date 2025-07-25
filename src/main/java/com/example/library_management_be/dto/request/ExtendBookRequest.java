package com.example.library_management_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExtendBookRequest {
    @NotNull(message = "newDueDate is required")
    private LocalDate newDueDate; // Ngày gia hạn mới
}
