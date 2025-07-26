package com.example.library_management_be.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedbackUpdateRequest {
    @NotBlank(message = "Content cannot be blank")
    private String content;

    @NotNull(message = "Rating cannot be null")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    private Integer rating;
}
