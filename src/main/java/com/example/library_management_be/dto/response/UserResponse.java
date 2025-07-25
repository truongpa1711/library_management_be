package com.example.library_management_be.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String email; // Email of the user
    private String fullName; // Full name of the user
    private String phoneNumber; // Phone number of the user
    private String address; // Address of the user
    private LocalDate dateOfBirth; // Date of birth of the user in ISO format (yyyy-MM-dd)
    private String role; // Role of the user (e.g., USER, ADMIN, LIBRARIAN)
}
