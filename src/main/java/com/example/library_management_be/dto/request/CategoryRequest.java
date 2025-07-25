package com.example.library_management_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name; // Tên danh mục

    private String description; // Mô tả danh mục (nếu cần)
    private String tags; // Thẻ gắn với danh mục, có thể là từ khóa hoặc thẻ mô tả

}
