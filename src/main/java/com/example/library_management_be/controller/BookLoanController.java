package com.example.library_management_be.controller;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.BookLoanFilterRequest;
import com.example.library_management_be.dto.request.BookLoanRequest;
import com.example.library_management_be.dto.request.BookReturnRequest;
import com.example.library_management_be.dto.request.ExtendBookRequest;
import com.example.library_management_be.dto.response.GetAllBookLoanResponse;
import com.example.library_management_be.service.BookLoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-loans")
@RequiredArgsConstructor
public class BookLoanController {
    private final BookLoanService bookLoanService;

    @PostMapping
    public ResponseEntity<BaseResponse<?>> borrowBook(Authentication authentication, @Valid @RequestBody BookLoanRequest request) {
        return ResponseEntity.ok(bookLoanService.borrowBook(authentication, request));
    }

    @PutMapping("/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> returnBook(@PathVariable Long id, @Valid @RequestBody BookReturnRequest request) {
        return ResponseEntity.ok(bookLoanService.returnBook(id, request));
    }

    @GetMapping("/loans")
    public ResponseEntity<BaseResponse<Page<GetAllBookLoanResponse>>> getAllLoans(
            BookLoanFilterRequest filter,
            @PageableDefault(page = 0, size = 10, sort = "borrowDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(bookLoanService.getAllLoans(filter, pageable));
    }

    @GetMapping("/my-loans")
    public ResponseEntity<BaseResponse<List<GetAllBookLoanResponse>>> getMyLoans(Authentication authentication) {
        String email = authentication.getName();

        List<GetAllBookLoanResponse> result = bookLoanService.getLoansByUserEmail(email);

        return ResponseEntity.ok(BaseResponse.<List<GetAllBookLoanResponse>>builder()
                .status("success")
                .message("Danh sách đơn mượn của người dùng")
                .data(result)
                .build());
    }

    // Giúp người dùng gia hạn thời gian mượn sách
    @PutMapping("/{id}/extend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> extendBook(
            @PathVariable Long id,
            @Valid @RequestBody ExtendBookRequest request) {
        return ResponseEntity.ok(bookLoanService.extendBookLoan(id, request));
    }

}
