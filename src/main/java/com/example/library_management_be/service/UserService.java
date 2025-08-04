package com.example.library_management_be.service;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.AdminUpdateUserRequest;
import com.example.library_management_be.dto.request.ChangePasswordRequest;
import com.example.library_management_be.dto.request.UserUpdateRequest;
import com.example.library_management_be.dto.response.AdminUserResponse;
import com.example.library_management_be.dto.response.UserResponse;
import com.example.library_management_be.entity.User;
import com.example.library_management_be.exception.UserException;
import com.example.library_management_be.mapper.UserMapper;
import com.example.library_management_be.repository.BlacklistTokenRepository;
import com.example.library_management_be.repository.UserRepository;
import com.example.library_management_be.utils.JwtUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.library_management_be.entity.BlacklistToken;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final BlacklistTokenRepository blacklistTokenRepository;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, BlacklistTokenRepository blacklistTokenRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.blacklistTokenRepository = blacklistTokenRepository;
        this.jwtUtils = jwtUtils;
    }

    public BaseResponse<UserResponse> getUserInfo(Authentication authentication) {
        String email= authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse userResponse = userMapper.toDto(user);
        return BaseResponse.<UserResponse>builder()
                .status("success")
                .message("User information retrieved successfully")
                .data(userResponse)
                .build();
    }

    public BaseResponse<UserResponse> updateUserInfo(Authentication authentication, UserUpdateRequest userUpdateRequest) {
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

        return BaseResponse.<UserResponse>builder()
                .status("success")
                .message("User information updated successfully")
                .data(userResponse)
                .build();
    }

    public BaseResponse<String> changePassword(Authentication authentication, ChangePasswordRequest changePasswordRequest) {
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

        return BaseResponse.<String>builder()
                .status("success")
                .message("Password changed successfully")
                .data("Password updated successfully")
                .build();
    }

    public BaseResponse<String> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateAccessToken(token)) {
            return BaseResponse.<String>builder()
                    .status("error")
                    .message("Token không hợp lệ hoặc không tồn tại")
                    .data(null)
                    .build();
        }
        BlacklistToken blacklistToken = new BlacklistToken();
        blacklistToken.setToken(token);
        blacklistTokenRepository.save(blacklistToken);
        return BaseResponse.<String>builder()
                .status("success")
                .message("Logout successful")
                .data("User logged out successfully")
                .build();
    }

    public BaseResponse<Page<AdminUserResponse>> getAllUsers(int page, int size, String orderBy, String direction, String name, String email, String role) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), orderBy));

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%"));
            }
            if (email != null && !email.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (role != null && !role.isEmpty()) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userRepository.findAll(spec, pageable);
        Page<AdminUserResponse> userResponses = userPage.map(userMapper::toAdminUserResponse);

        return new BaseResponse<>("success", "Lấy danh sách người dùng thành công", userResponses);
    }

    public BaseResponse<AdminUserResponse> updateUserByAdmin(Long id, AdminUpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException.UserNotFoundException("Không tìm thấy người dùng với ID: " + id));

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(request.getRole());
        user.setActive(request.getIsActive());
        user.set_verified(request.getIsVerified());

        userRepository.save(user);

        AdminUserResponse response = userMapper.toAdminUserResponse(user);
        return new BaseResponse<>("success", "Cập nhật người dùng thành công", response);
    }

    public BaseResponse<Page<AdminUserResponse>> searchUserByEmail(String email) {
        System.out.println("Searching for users with email: " + email);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "email")); // Default pagination

        Specification<User> spec = (root, query, cb) ->
                cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");

        Page<User> userPage = userRepository.findAll(spec, pageable);
        Page<AdminUserResponse> userResponses = userPage.map(userMapper::toAdminUserResponse);

        return BaseResponse.<Page<AdminUserResponse>>builder()
                .status("success")
                .message("Search completed successfully")
                .data(userResponses)
                .build();
    }



    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }


}
