package com.example.library_management_be.dto.request;

import com.example.library_management_be.entity.enums.EFeedbackStatus;
import lombok.Data;

@Data
public class FeedbackStatusRequest {
    private EFeedbackStatus status; // APPROVED hoặc REJECT
}
