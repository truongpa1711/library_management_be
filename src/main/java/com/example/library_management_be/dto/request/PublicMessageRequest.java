package com.example.library_management_be.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicMessageRequest {
    private long senderId; // ID của người gửi
    private String content; // Nội dung tin nhắn
}
