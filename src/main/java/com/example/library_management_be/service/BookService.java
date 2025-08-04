package com.example.library_management_be.service;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.BookRequest;
import com.example.library_management_be.dto.response.BookResponse;
import com.example.library_management_be.dto.response.BookStatisticsResponse;
import com.example.library_management_be.dto.response.SimpleCount;
import com.example.library_management_be.entity.Book;
import com.example.library_management_be.entity.Category;
import com.example.library_management_be.entity.enums.EBookStatus;
import com.example.library_management_be.exception.BookException;
import com.example.library_management_be.mapper.BookMapper;
import com.example.library_management_be.repository.BookRepository;
import com.example.library_management_be.repository.CategoryRepository;
import com.example.library_management_be.exception.CategoryException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public BaseResponse<BookResponse> createBook(BookRequest bookRequest) {
        Optional<Book> existing = bookRepository.findByTitle(bookRequest.getTitle());
        if (existing.isPresent()) {
            throw new BookException.BookAlreadyExistsException("Sách với tiêu đề '" + bookRequest.getTitle() + "' đã tồn tại");
        }

        // Lấy danh sách category từ ID
        List<Category> categories = categoryRepository.findAllById(bookRequest.getCategoryIds());
        if (categories.size() != bookRequest.getCategoryIds().size()) {
            throw new CategoryException.CategoryNotFoundException("Một hoặc nhiều danh mục không tồn tại");
        }

        // Chuyển status từ chuỗi sang enum
        EBookStatus status;
        try {
            status = EBookStatus.valueOf(bookRequest.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BookException.InvalidBookStatusException("Trạng thái sách không hợp lệ: " + bookRequest.getStatus());
        }
        Book book = bookMapper.toEntity(bookRequest);
        book.setCategories(categories);
        book.setStatus(status);

        bookRepository.save(book);
        BookResponse bookResponse = bookMapper.toDto(book);

        return BaseResponse.<BookResponse>builder()
                .status("success")
                .message("Book created successfully")
                .data(bookResponse)
                .build();
    }

    @Transactional
    public BaseResponse<BookResponse> updateBook(Long id, BookRequest bookRequest) {
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isEmpty()) {
            throw new BookException.BookNotFoundException("Sách không tồn tại với ID: " + id);
        }

        Book book = existingBook.get();


        // Cập nhật danh sách category
        List<Category> categories = categoryRepository.findAllById(bookRequest.getCategoryIds());
        if (categories.size() != bookRequest.getCategoryIds().size()) {
            throw new CategoryException.CategoryNotFoundException("Một hoặc nhiều danh mục không tồn tại");
        }

        book.setCategories(categories);

        // Cập nhật status
        try {
            EBookStatus status = EBookStatus.valueOf(bookRequest.getStatus().toUpperCase());
            book.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new BookException.InvalidBookStatusException("Trạng thái sách không hợp lệ: " + bookRequest.getStatus());
        }
        bookMapper.updateEntityFromRequest(bookRequest, book);
        bookRepository.save(book);
        BookResponse bookResponse = bookMapper.toDto(book);

        return BaseResponse.<BookResponse>builder()
                .status("success")
                .message("Book updated successfully")
                .data(bookResponse)
                .build();
    }

    @Transactional
    public BaseResponse<String> deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookException.BookNotFoundException("Sách không tồn tại với ID: " + id));

        // Xóa các liên kết trong bảng trung gian book_category
        book.getCategories().clear(); // xóa liên kết
        bookRepository.save(book);    // cập nhật lại liên kết đã clear

        // Xóa bản thân Book
        bookRepository.delete(book);
        return BaseResponse.<String>builder()
                .status("success")
                .message("Book deleted successfully")
                .data("Book with ID " + id + " has been deleted")
                .build();
    }

    public BaseResponse<BookResponse> getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookException.BookNotFoundException("Sách không tồn tại với ID: " + id));

        BookResponse bookResponse = bookMapper.toDto(book);
        return BaseResponse.<BookResponse>builder()
                .status("success")
                .message("Book retrieved successfully")
                .data(bookResponse)
                .build();
    }

    @Transactional
    public Page<BookResponse> getAllBooks(String title, String author, String genre, String status,
                                          int page, int size, String orderBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.DESC);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, orderBy));

        Specification<Book> spec = null;

        if (StringUtils.hasText(title)) {
            Specification<Book> titleSpec = (root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
            spec = (spec == null) ? titleSpec : spec.and(titleSpec);
        }

        if (StringUtils.hasText(author)) {
            Specification<Book> authorSpec = (root, query, cb) ->
                    cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
            spec = (spec == null) ? authorSpec : spec.and(authorSpec);
        }

        if (StringUtils.hasText(genre)) {
            Specification<Book> genreSpec = (root, query, cb) ->
                    cb.like(cb.lower(root.get("genre")), "%" + genre.toLowerCase() + "%");
            spec = (spec == null) ? genreSpec : spec.and(genreSpec);
        }

        if (StringUtils.hasText(status)) {
            try {
                EBookStatus statusEnum = EBookStatus.valueOf(status.toUpperCase());
                Specification<Book> statusSpec = (root, query, cb) ->
                        cb.equal(root.get("status"), statusEnum);
                spec = (spec == null) ? statusSpec : spec.and(statusSpec);
            } catch (IllegalArgumentException e) {
                throw new BookException.InvalidBookStatusException("Trạng thái sách không hợp lệ");
            }
        }

        Page<Book> bookPage = bookRepository.findAll(spec, pageable);
        return bookPage.map(bookMapper::toDto);
    }

    @Transactional
    public Page<BookResponse> getBooksByCategory(Long categoryId, int page, int size, String orderBy, String direction) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException.CategoryNotFoundException("Danh mục không tồn tại với ID: " + categoryId));

        Sort.Direction sortDirection = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, orderBy));

        Page<Book> bookPage = bookRepository.findAll(
                (root, query, cb) -> cb.isMember(category, root.get("categories")), pageable);

        return bookPage.map(bookMapper::toDto);
    }

    public List<BookResponse> getTopBorrowedBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Book> books = bookRepository.findTopBorrowed(pageable);
        return books.stream().map(bookMapper::toDto).toList();
    }

    public List<BookResponse> getSimilarBooks(Long bookId, int limit) {
        Book target = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException.BookNotFoundException("Không tìm thấy sách"));

        Pageable pageable = PageRequest.of(0, limit);
        List<Book> similar = bookRepository.findSimilarBooks(
                bookId, target.getGenre(), target.getAuthor(), pageable
        );

        return similar.stream().map(bookMapper::toDto).toList();
    }

    public BookStatisticsResponse getStatistics() {
        long totalBooks = bookRepository.countTotalBooks();
        Long totalBorrowed = bookRepository.countTotalBorrowed();
        if (totalBorrowed == null) totalBorrowed = 0L;

        Pageable top5 = PageRequest.of(0, 5);

        List<SimpleCount> topAuthors = bookRepository.findTopAuthors(top5)
                .stream().map(o -> new SimpleCount((String)o[0], (Long)o[1])).toList();

        List<SimpleCount> topGenres = bookRepository.findTopGenres(top5)
                .stream().map(o -> new SimpleCount((String)o[0], (Long)o[1])).toList();

        List<BookResponse> mostBorrowed = bookRepository.findTop5ByOrderByBorrowCountDesc()
                .stream().map(bookMapper::toDto).toList();

        return BookStatisticsResponse.builder()
                .totalBooks(totalBooks)
                .totalBorrowed(totalBorrowed)
                .topAuthors(topAuthors)
                .topGenres(topGenres)
                .mostBorrowedBooks(mostBorrowed)
                .build();
    }

}
