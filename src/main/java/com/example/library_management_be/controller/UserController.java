package com.example.library_management_be.controller;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.AdminUpdateUserRequest;
import com.example.library_management_be.dto.request.ChangePasswordRequest;
import com.example.library_management_be.dto.request.UserUpdateRequest;
import com.example.library_management_be.dto.response.AdminUserResponse;
import com.example.library_management_be.dto.response.UserResponse;
import com.example.library_management_be.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


    @GetMapping("/get-all-users")
    public ResponseEntity<BaseResponse<Page<AdminUserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(userService.getAllUsers(page, size, orderBy, direction, name, email, role));
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<AdminUserResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid AdminUpdateUserRequest adminUpdateUserRequest) {
        return ResponseEntity.ok(userService.updateUserByAdmin(id, adminUpdateUserRequest));
    }

    @GetMapping("/searchByEmail")
    public ResponseEntity<BaseResponse<Page<AdminUserResponse>>> searchUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.searchUserByEmail(email));
    }



}
