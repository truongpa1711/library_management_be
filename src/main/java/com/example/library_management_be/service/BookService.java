package com.example.library_management_be.service;

import com.example.library_management_be.dto.BaseRespone;
import com.example.library_management_be.dto.request.BookRequest;
import com.example.library_management_be.dto.response.BookResponse;
import com.example.library_management_be.entity.Book;
import com.example.library_management_be.entity.Category;
import com.example.library_management_be.entity.enums.EBookStatus;
import com.example.library_management_be.mapper.BookMapper;
import com.example.library_management_be.repository.BookRepository;
import com.example.library_management_be.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookMapper = bookMapper;
    }

    @Transactional
    public BaseRespone<BookResponse> createBook(BookRequest bookRequest) {
        Optional<Book> existing = bookRepository.findByTitle(bookRequest.getTitle());
        if (existing.isPresent()) {
            return BaseRespone.<BookResponse>builder()
                    .status("error")
                    .message("Book with this title already exists")
                    .build();
        }

        // Lấy danh sách category từ ID
        List<Category> categories = categoryRepository.findAllById(bookRequest.getCategoryIds());
        if (categories.size() != bookRequest.getCategoryIds().size()) {
            return BaseRespone.<BookResponse>builder()
                    .status("error")
                    .message("Một hoặc nhiều danh mục không tồn tại")
                    .build();
        }

        // Chuyển status từ chuỗi sang enum
        EBookStatus status;
        try {
            status = EBookStatus.valueOf(bookRequest.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            return BaseRespone.<BookResponse>builder()
                    .status("error")
                    .message("Trạng thái sách không hợp lệ")
                    .build();
        }
        Book book = bookMapper.toEntity(bookRequest);
        book.setCategories(categories);
        book.setStatus(status);

        bookRepository.save(book);
        BookResponse bookResponse = bookMapper.toDto(book);

        return BaseRespone.<BookResponse>builder()
                .status("success")
                .message("Book created successfully")
                .data(bookResponse)
                .build();
    }
}
