package com.example.library_management_be.service;

import com.example.library_management_be.dto.BaseRespone;
import com.example.library_management_be.dto.request.LoginRequest;
import com.example.library_management_be.dto.response.LoginResponse;
import com.example.library_management_be.repository.UserRepository;
import com.example.library_management_be.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    public BaseRespone<LoginResponse> login(LoginRequest loginRequest) {
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));
            String accessToken = jwtUtils.generateAccessToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(authentication);
            LoginResponse loginResponse = LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            return BaseRespone.<LoginResponse>builder()
                    .status("success")
                    .message("Login successful")
                    .data(loginResponse)
                    .build();
        }catch (Exception e) {
            return BaseRespone.<LoginResponse>builder()
                    .status("error")
                    .message("Authentication failed: " + e.getMessage())
                    .build();
        }
    }


}
