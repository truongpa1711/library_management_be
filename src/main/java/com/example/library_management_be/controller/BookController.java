package com.example.library_management_be.controller;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.BookRequest;
import com.example.library_management_be.dto.response.BookResponse;
import com.example.library_management_be.dto.response.BookStatisticsResponse;
import com.example.library_management_be.entity.Book;
import com.example.library_management_be.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<BookResponse>> createBook(@Valid @RequestBody BookRequest bookRequest) {
        // Validate the book request
        if(bookRequest.getAvailableQuantity()> bookRequest.getTotalQuantity()) {
            return ResponseEntity.badRequest().body(
                BaseResponse.<BookResponse>builder()
                    .status("error")
                    .message("Số lượng có sẵn không thể lớn hơn tổng số lượng")
                    .build()
            );
        }
        return ResponseEntity.ok(bookService.createBook(bookRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<BookResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest bookRequest) {
        // Validate the book request
        if(bookRequest.getAvailableQuantity() > bookRequest.getTotalQuantity()) {
            return ResponseEntity.badRequest().body(
                BaseResponse.<BookResponse>builder()
                    .status("error")
                    .message("Số lượng có sẵn không thể lớn hơn tổng số lượng")
                    .build()
            );
        }
        return ResponseEntity.ok(bookService.updateBook(id, bookRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> deleteBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.deleteBook(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<BookResponse>> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<BookResponse>>> getAllBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Page<BookResponse> books = bookService.getAllBooks(title, author, genre, status, page, size, orderBy, direction);
        return ResponseEntity.ok(
            BaseResponse.<Page<BookResponse>>builder()
                .status("success")
                .message("Danh sách sách")
                .data(books)
                .build()
        );
    }

    @GetMapping("/get-by-category/{categoryId}")
    public ResponseEntity<BaseResponse<Page<BookResponse>>> getBooksByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Page<BookResponse> books = bookService.getBooksByCategory(categoryId, page, size, orderBy, direction);
        return ResponseEntity.ok(
            BaseResponse.<Page<BookResponse>>builder()
                .status("success")
                .message("Danh sách sách theo danh mục")
                .data(books)
                .build()
        );
    }

    @GetMapping("/top-borrowed")
    public ResponseEntity<BaseResponse<List<BookResponse>>> getTopBorrowedBooks(
            @RequestParam(defaultValue = "5") int limit) {
        List<BookResponse> topBorrowedBooks = bookService.getTopBorrowedBooks(limit);
        return ResponseEntity.ok(
            BaseResponse.<List<BookResponse>>builder()
                .status("success")
                .message("Danh sách sách mượn nhiều nhất")
                .data(topBorrowedBooks)
                .build()
        );
    }

    @GetMapping("/similar/{id}")
    public ResponseEntity<BaseResponse<List<BookResponse>>> getSimilarBooks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "5") int limit) {
        List<BookResponse> similarBooks = bookService.getSimilarBooks(id, limit);
        return ResponseEntity.ok(
            BaseResponse.<List<BookResponse>>builder()
                .status("success")
                .message("Danh sách sách tương tự")
                .data(similarBooks)
                .build()
        );
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<BookStatisticsResponse>> getStatistics() {
        BookStatisticsResponse stats = bookService.getStatistics();
        return ResponseEntity.ok(
                BaseResponse.<BookStatisticsResponse>builder()
                        .status("success")
                        .message("Thống kê sách")
                        .data(stats)
                        .build()
        );
    }

//    @GetMapping("/export")
//    @PreAuthorize("hasRole('ADMIN')")

}
