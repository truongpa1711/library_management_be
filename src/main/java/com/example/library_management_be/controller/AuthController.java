package com.example.library_management_be.controller;

import com.example.library_management_be.dto.BaseRespone;
import com.example.library_management_be.dto.request.*;
import com.example.library_management_be.dto.response.LoginResponse;
import com.example.library_management_be.dto.response.RefreshTokenResponse;
import com.example.library_management_be.exception.UserException;
import com.example.library_management_be.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseRespone<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }


    @PostMapping("/register")
    public ResponseEntity<BaseRespone<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        BaseRespone<String> response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<BaseRespone<String>> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<BaseRespone<String>> resendVerificationEmail(@RequestBody ResendVerificationRequest resendVerificationRequest) {
        return ResponseEntity.ok(authService.resendVerificationEmail(resendVerificationRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<BaseRespone<RefreshTokenResponse>> refreshToken(@RequestBody RefreshTokenRequest refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<BaseRespone<String>> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return ResponseEntity.ok(authService.forgotPassword(forgotPasswordRequest));
    }

}
