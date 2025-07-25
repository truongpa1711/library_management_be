package com.example.library_management_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NonNull
    @NotBlank(message = "Email is required")
    private String email; // Email of the user
    @NotBlank(message = "Password is required")
    private String password; // Password of the user
    @NotBlank(message = "Full name is required")
    private String fullName; // Full name of the user
    @NotBlank(message = "Phone number is required")
    private String phoneNumber; // Phone number of the user
    @NotBlank(message = "Address is required")
    private String address; // Address of the user
    @Past(message = "Ngày sinh phải trong quá khứ")
    private LocalDate dateOfBirth; // Date of birth of the user
}
