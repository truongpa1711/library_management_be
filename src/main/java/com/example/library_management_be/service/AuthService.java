package com.example.library_management_be.service;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.*;
import com.example.library_management_be.dto.response.LoginResponse;
import com.example.library_management_be.dto.response.RefreshTokenResponse;
import com.example.library_management_be.entity.User;
import com.example.library_management_be.event.ForgotPasswordEvent;
import com.example.library_management_be.event.UserRegistrationEvent;
import com.example.library_management_be.exception.UserException;
import com.example.library_management_be.repository.UserRepository;
import com.example.library_management_be.utils.JwtUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;

    public AuthService(UserRepository userRepository, JwtUtils jwtUtils, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public BaseResponse<LoginResponse> login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserException.UserNotFoundException("User not found"));

        if (!user.is_verified()) {
            throw new UserException.UserNotVerifiedException("User account is not verified");
        }
        if (!user.isActive()) {
            throw new UserException.UserNotActiveException("User account is not active");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        String accessToken = jwtUtils.generateAccessToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        return BaseResponse.<LoginResponse>builder()
                .status("success")
                .message("Login successful")
                .data(new LoginResponse(accessToken, refreshToken))
                .build();
    }

    public BaseResponse<String> register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserException.UserAlreadyExistsException(registerRequest.getEmail());
        }
        if (registerRequest.getPassword().length() < 8) {
            throw new UserException.WeakPasswordException();
        }
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        String validationToken = jwtUtils.generateTokenValidation(registerRequest.getEmail());

        User newUser = new User()
                .setEmail(registerRequest.getEmail())
                .setPassword(encodedPassword)
                .setFullName(registerRequest.getFullName())
                .setPhoneNumber(registerRequest.getPhoneNumber())
                .setAddress(registerRequest.getAddress())
                .setDateOfBirth(registerRequest.getDateOfBirth())
                .set_verified(false)
                .setVerifiedToken(validationToken);

        userRepository.save(newUser);

        // Publish an event to send the verification email
        applicationEventPublisher.publishEvent(new UserRegistrationEvent(this,newUser));

        return BaseResponse.<String>builder()
                .status("success")
                .message("User registered successfully")
                .data("Registration successful")
                .build();
    }
    public BaseResponse<String> verifyEmail(String token) {
        String email = jwtUtils.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException("User not found with email: " + email));

        if (user.is_verified()) {
            throw new UserException.UserAlreadyVerifiedException("User account is already verified");
        }

        if (!jwtUtils.validateTokenValidation(token, email)) {
            throw new UserException.InvalidTokenException("Invalid or expired verification token");
        }

        user.set_verified(true);
        user.setVerifiedToken(null);
        userRepository.save(user);

        return BaseResponse.<String>builder()
                .status("success")
                .message("Email verification successful")
                .data("Email verified successfully")
                .build();
    }

    public BaseResponse<String> resendVerificationEmail(ResendVerificationRequest resendVerificationRequest) {
        String email = resendVerificationRequest.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException("User not found with email: " + email));

        if (user.is_verified()) {
            throw new UserException.UserAlreadyVerifiedException(email);
        }

        String validationToken = jwtUtils.generateTokenValidation(email);
        user.setVerifiedToken(validationToken);
        userRepository.save(user);

        // Publish an event to send the verification email
        applicationEventPublisher.publishEvent(new UserRegistrationEvent(this, user));

        return BaseResponse.<String>builder()
                .status("success")
                .message("Verification email resent successfully")
                .data("Verification email sent")
                .build();
    }

    public BaseResponse<RefreshTokenResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new UserException.InvalidTokenException("Invalid or expired refresh token");
        }

        String email = jwtUtils.getUsernameFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException("User not found with email: " + email));

        if (!user.is_verified()) {
            throw new UserException.UserNotVerifiedException("User account is not verified");
        }

        String newAccessToken = jwtUtils.generateAccessToken(user.getEmail());
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return BaseResponse.<RefreshTokenResponse>builder()
                .status("success")
                .message("Tokens refreshed successfully")
                .data(new RefreshTokenResponse(newAccessToken, newRefreshToken))
                .build();
    }
    public BaseResponse<String> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        String email = forgotPasswordRequest.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException("User not found with email: " + email));

        String randomPassword = generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(randomPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Publish an event to send the reset password email
        applicationEventPublisher.publishEvent(new ForgotPasswordEvent(this, email,randomPassword));

        return BaseResponse.<String>builder()
                .status("success")
                .message("Reset password email sent successfully")
                .data("Reset password email sent")
                .build();
    }



    //=================
    private String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*";

        String allChars = upper + lower + digits + symbols;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(allChars.length());
            password.append(allChars.charAt(index));
        }

        return password.toString();
    }


}
