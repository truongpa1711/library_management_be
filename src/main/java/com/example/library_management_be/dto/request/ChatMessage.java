package com.example.library_management_be.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private Long chatId;
    private int senderId;
    private String content;
    private LocalDateTime timestamp;
//    private String attachmentUrl;
//    private String attachmentType;
}