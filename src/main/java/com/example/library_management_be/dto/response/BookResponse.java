package com.example.library_management_be.dto.response;

import com.example.library_management_be.entity.enums.EBookStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private String isbn ;
    private String publisher;
    private int publicationYear;
    private String description;
    private String genre;
    private int availableQuantity;
    private int totalQuantity;
    private String location;
    private EBookStatus status;
    private Double averageRating;
    private int totalRatings;
    private String imageUrl;

    private List<CategoryDto> categories;

    @Getter
    @Setter
    public static class CategoryDto {
        private Long id;
        private String name;
    }
}
