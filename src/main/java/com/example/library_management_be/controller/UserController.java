package com.example.library_management_be.controller;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.ChangePasswordRequest;
import com.example.library_management_be.dto.request.UserUpdateRequest;
import com.example.library_management_be.dto.response.UserResponse;
import com.example.library_management_be.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<UserResponse>> getUserInfo(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserInfo(authentication));
    }

    @PutMapping
    public ResponseEntity<BaseResponse<UserResponse>> updateUserInfo(Authentication authentication,
                                                                     @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(userService.updateUserInfo(authentication, userUpdateRequest));
    }


    @PutMapping("/change-password")
    public ResponseEntity<BaseResponse<String>> changePassword(Authentication authentication,
                                                               @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(userService.changePassword(authentication, changePasswordRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<String>> logout(Authentication authentication, HttpServletRequest request) {
        return ResponseEntity.ok(userService.logout(request));
    }

}
