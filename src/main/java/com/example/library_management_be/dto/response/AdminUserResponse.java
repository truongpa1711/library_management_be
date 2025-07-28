package com.example.library_management_be.dto.response;

import com.example.library_management_be.entity.enums.ERole;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AdminUserResponse {
    private Long id;
    private String email;
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String address;
    private ERole role;
    private boolean isActive;
    private boolean is_verified;
}
