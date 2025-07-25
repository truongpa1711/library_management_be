package com.example.library_management_be.dto.response;

import lombok.Data;

@Data
public class CategoryResponse {
    private Long id; // ID của danh mục
    private String name; // Tên danh mục
    private String description; // Mô tả danh mục (nếu cần)
    private String tags; // Thẻ gắn với danh mục, có thể là từ khóa hoặc thẻ mô tả

    public CategoryResponse(Long id, String name, String description, String tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = tags;
    }
}
