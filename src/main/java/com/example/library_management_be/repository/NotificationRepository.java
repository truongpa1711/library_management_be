package com.example.library_management_be.repository;

import com.example.library_management_be.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Thêm các phương thức truy vấn nếu cần
}
