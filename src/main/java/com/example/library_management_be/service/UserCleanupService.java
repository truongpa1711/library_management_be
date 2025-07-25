package com.example.library_management_be.service;

import com.example.library_management_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class UserCleanupService {

    private final UserRepository userRepository;

    @Autowired
    public UserCleanupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(fixedDelay = 60* 60 * 1000) // 1 giờ
    public void deleteUnverifiedUsers() {
        Instant expiryTime = Instant.now().minusSeconds(24 * 60 * 60); // 24 giờ trước
        userRepository.deleteUnverifiedUsersBefore(expiryTime);
        System.out.println("Đã xóa user chưa xác thực trước: " + expiryTime);
    }
}
