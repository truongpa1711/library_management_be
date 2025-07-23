package com.example.library_management_be.entity;

import com.example.library_management_be.entity.enums.EActionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class AuditLog extends BaseEntity{
    // Loại entity bị thao tác (BOOK, USER, LOAN,...)
    @Column(nullable = false)
    private String entityType;

    // ID của entity bị thao tác
    @Column(nullable = false)
    private Long entityId;

    // Hành động thực hiện (CREATE, UPDATE, DELETE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EActionType action;

    // Thông tin mô tả hành động
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    // Thông tin người thực hiện hành động
    @Column(nullable = false)
    private String performedBy;

    // Thời gian thực hiện thao tác
    @Column(nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    // Chi tiết thay đổi (ví dụ JSON: {"status":"OLD" → "NEW"})
    @Column(columnDefinition = "TEXT")
    private String changeDetail;

}
