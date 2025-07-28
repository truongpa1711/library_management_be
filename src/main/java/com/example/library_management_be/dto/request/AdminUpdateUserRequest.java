package com.example.library_management_be.dto.request;

import com.example.library_management_be.entity.enums.ERole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AdminUpdateUserRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private LocalDate dateOfBirth;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String address;

    @NotNull
    private ERole role;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Boolean isVerified;
}
