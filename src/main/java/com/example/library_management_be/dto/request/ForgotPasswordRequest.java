package com.example.library_management_be.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email; // Email address of the user requesting a password reset
}
