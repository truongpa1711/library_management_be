package com.example.library_management_be.service;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.CategoryRequest;
import com.example.library_management_be.dto.response.CategoryResponse;
import com.example.library_management_be.entity.Category;
import com.example.library_management_be.exception.CategoryException;
import com.example.library_management_be.mapper.CategoryMapper;
import com.example.library_management_be.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public BaseResponse<CategoryResponse> createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.findByName(categoryRequest.getName()) != null) {
            throw new CategoryException.CategoryAlreadyExistsException("Category with name '" + categoryRequest.getName() + "' already exists");
        }
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setTags(categoryRequest.getTags());
        categoryRepository.save(category);

        CategoryResponse categoryResponse
                = categoryMapper.toDto(category);

        return BaseResponse.<CategoryResponse>builder()
                .status("success")
                .message("Category created successfully")
                .data(categoryResponse)
                .build();

    }
    public BaseResponse<CategoryResponse> updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException.CategoryNotFoundException("Category not found"));

        if (categoryRequest.getName() != null) {
            category.setName(categoryRequest.getName());
        }
        if (categoryRequest.getDescription() != null) {
            category.setDescription(categoryRequest.getDescription());
        }
        if (categoryRequest.getTags() != null) {
            category.setTags(categoryRequest.getTags());
        }
        Category updatedCategory = categoryRepository.save(category);
        CategoryResponse categoryResponse = categoryMapper.toDto(updatedCategory);

        return BaseResponse.<CategoryResponse>builder()
                .status("success")
                .message("Category updated successfully")
                .data(categoryResponse)
                .build();
    }
    public BaseResponse<String> deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException.CategoryNotFoundException("Category not found"));

        categoryRepository.delete(category);
        return BaseResponse.<String>builder()
                .status("success")
                .message("Category deleted successfully")
                .data("Category with ID " + id + " has been deleted")
                .build();
    }
    public BaseResponse<CategoryResponse> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException.CategoryNotFoundException("Category not found"));

        CategoryResponse categoryResponse = categoryMapper.toDto(category);
        return BaseResponse.<CategoryResponse>builder()
                .status("success")
                .message("Category retrieved successfully")
                .data(categoryResponse)
                .build();
    }
    public BaseResponse<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> categoryResponses = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());

        return BaseResponse.<List<CategoryResponse>>builder()
                .status("success")
                .message("All categories retrieved successfully")
                .data(categoryResponses)
                .build();
    }
}
