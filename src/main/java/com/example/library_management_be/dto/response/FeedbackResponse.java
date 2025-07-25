package com.example.library_management_be.dto.response;

import com.example.library_management_be.entity.enums.EFeedbackStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackResponse {
    private Long id;
    private Long userId;
    private Long bookId;
    private String userEmail;
    private String bookTitle;
    private String content;
    private Integer rating;
    private String reply;
    private Boolean isEditable;
    private EFeedbackStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime repliedDate;
}
