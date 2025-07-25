package com.example.library_management_be.service;

import com.example.library_management_be.dto.BaseRespone;
import com.example.library_management_be.dto.request.ChangePasswordRequest;
import com.example.library_management_be.dto.request.UserUpdateRequest;
import com.example.library_management_be.dto.response.UserResponse;
import com.example.library_management_be.entity.User;
import com.example.library_management_be.exception.UserException;
import com.example.library_management_be.mapper.UserMapper;
import com.example.library_management_be.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public BaseRespone<UserResponse> getUserInfo(Authentication authentication) {
        String email= authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse userResponse = userMapper.toDto(user);
        return BaseRespone.<UserResponse>builder()
                .status("success")
                .message("User information retrieved successfully")
                .data(userResponse)
                .build();
    }

    public BaseRespone<UserResponse> updateUserInfo(Authentication authentication, UserUpdateRequest userUpdateRequest) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user fields based on the request
        if (userUpdateRequest.getFullName() != null) {
            user.setFullName(userUpdateRequest.getFullName());
        }
        if (userUpdateRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        }
        if (userUpdateRequest.getAddress() != null) {
            user.setAddress(userUpdateRequest.getAddress());
        }
        if (userUpdateRequest.getDateOfBirth() != null) {
            user.setDateOfBirth(userUpdateRequest.getDateOfBirth());
        }
        // Save updated user
        User updatedUser = userRepository.save(user);
        UserResponse userResponse = userMapper.toDto(updatedUser);

        return BaseRespone.<UserResponse>builder()
                .status("success")
                .message("User information updated successfully")
                .data(userResponse)
                .build();
    }

    public BaseRespone<String> changePassword(Authentication authentication, ChangePasswordRequest changePasswordRequest) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException("User not found"));

        String newPassword = changePasswordRequest.getNewPassword();
        String oldPassword = changePasswordRequest.getOldPassword();
        if (newPassword == null || newPassword.isEmpty()) {
            throw new UserException.WeakPasswordException();
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserException.InvalidTokenException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return BaseRespone.<String>builder()
                .status("success")
                .message("Password changed successfully")
                .data("Password updated successfully")
                .build();
    }

}
