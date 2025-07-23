package com.example.library_management_be.entity;

import com.example.library_management_be.entity.enums.ENotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Notification extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người dùng nhận thông báo

    @Column(name = "title", nullable = false)
    private String title; // Tiêu đề thông báo

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // Nội dung thông báo

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ENotificationType type;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false; // Trạng thái đã đọc hay chưa

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
}
