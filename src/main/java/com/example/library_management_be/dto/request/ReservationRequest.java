package com.example.library_management_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {
    @NotNull(message = "Book ID is required")
    private Long bookId;
}
