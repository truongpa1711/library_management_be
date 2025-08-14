package com.example.library_management_be.repository;

import com.example.library_management_be.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Các phương thức truy vấn tùy chỉnh nếu cần
}
