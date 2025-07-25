package com.example.library_management_be.dto.request;

import com.example.library_management_be.entity.enums.EBookCondition;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookReturnRequest {
    @NotNull(message = "Book Condition cannot be null")
    private EBookCondition bookCondition;
}
