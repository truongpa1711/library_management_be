package com.example.library_management_be.mapper;

import com.example.library_management_be.dto.response.FeedbackResponse;
import com.example.library_management_be.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "book.id", target = "bookId")
    FeedbackResponse toDto(Feedback feedback);
}
