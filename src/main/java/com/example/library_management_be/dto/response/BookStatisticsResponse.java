package com.example.library_management_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookStatisticsResponse {
    private long totalBooks;
    private long totalBorrowed;
    private List<SimpleCount> topAuthors;
    private List<SimpleCount> topGenres;
    private List<BookResponse> mostBorrowedBooks;
}

