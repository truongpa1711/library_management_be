package com.example.library_management_be.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String email; // Username of the user
    private String password; // Password of the user
}
