package com.example.library_management_be.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {
    private String newPassword; // New password to be set for the user
    private String oldPassword; // Old password to verify the user's identity
}
