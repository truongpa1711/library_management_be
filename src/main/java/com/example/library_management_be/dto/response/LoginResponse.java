package com.example.library_management_be.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken; // JWT token for authentication
    private String refreshToken; // Token to refresh the access token
}
