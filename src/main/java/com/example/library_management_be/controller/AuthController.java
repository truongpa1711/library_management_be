package com.example.library_management_be.controller;

import com.example.library_management_be.dto.BaseRespone;
import com.example.library_management_be.dto.request.LoginRequest;
import com.example.library_management_be.dto.response.LoginResponse;
import com.example.library_management_be.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public BaseRespone<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Login request received: " + loginRequest);
            return authService.login(loginRequest);
        } catch (Exception e) {
            return BaseRespone.<LoginResponse>builder()
                    .status("error")
                    .message("Login failed: " + e.getMessage())
                    .build();
        }
    }

}
