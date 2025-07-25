package com.example.library_management_be.controller;

import com.example.library_management_be.dto.BaseRespone;
import com.example.library_management_be.dto.request.BookRequest;
import com.example.library_management_be.dto.response.BookResponse;
import com.example.library_management_be.service.BookService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseRespone<BookResponse>> createBook(@Valid @RequestBody BookRequest bookRequest) {
        return ResponseEntity.ok(bookService.createBook(bookRequest));
    }

//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<BaseRespone<AddBookResponse>> updateBook(
//            @PathVariable Long id,
//            @RequestBody AddBookRequest bookRequest) {
//        return ResponseEntity.ok(bookService.updateBook(id, bookRequest));
//    }

}
