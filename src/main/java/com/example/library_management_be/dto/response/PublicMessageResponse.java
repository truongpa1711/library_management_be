package com.example.library_management_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicMessageResponse {
    private String senderName;
    private String senderRole;
    private String content;
    private LocalDateTime timestamp;
}
