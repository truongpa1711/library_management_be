package com.example.library_management_be.mapper;

import com.example.library_management_be.dto.response.CategoryResponse;
import com.example.library_management_be.entity.Category;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toDto(Category category);
}
