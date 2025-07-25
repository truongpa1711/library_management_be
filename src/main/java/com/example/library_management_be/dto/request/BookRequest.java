package com.example.library_management_be.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    private String author;

    @NotBlank(message = "ISBN không được để trống")
    @Size(min = 2, max = 13, message = "ISBN phải có độ dài từ 2 đến 13 ký tự")
    private String isbn;

    private String publisher;

    @Min(value = 1000, message = "Năm xuất bản không hợp lệ")
    @Max(value = 2100, message = "Năm xuất bản không hợp lệ")
    private int publicationYear;

    @Size(max = 1000)
    private String description;
    private String genre;

    @Min(value = 0, message = "Số lượng có sẵn không được âm")
    private int availableQuantity;

    @Min(value = 1, message = "Tổng số lượng phải ít nhất là 1")
    private int totalQuantity;

    private String location;

    @NotNull(message = "Trạng thái sách là bắt buộc")
    private String status;

    private String imageUrl;

    private List<Long> categoryIds; // Danh sách ID category được chọn

}
