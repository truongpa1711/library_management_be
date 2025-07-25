package com.example.library_management_be.service;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.BookLoanResponse;
import com.example.library_management_be.dto.request.BookLoanFilterRequest;
import com.example.library_management_be.dto.request.BookLoanRequest;
import com.example.library_management_be.dto.request.BookReturnRequest;
import com.example.library_management_be.dto.request.ExtendBookRequest;
import com.example.library_management_be.dto.response.GetAllBookLoanResponse;
import com.example.library_management_be.entity.Book;
import com.example.library_management_be.entity.BookLoan;
import com.example.library_management_be.entity.Fine;
import com.example.library_management_be.entity.User;
import com.example.library_management_be.entity.enums.EBookCondition;
import com.example.library_management_be.entity.enums.EFineReason;
import com.example.library_management_be.entity.enums.EFineStatus;
import com.example.library_management_be.entity.enums.ELoanStatus;
import com.example.library_management_be.exception.BookException;
import com.example.library_management_be.exception.UserException;
import com.example.library_management_be.mapper.BookLoanMapper;
import com.example.library_management_be.repository.BookLoanRepository;
import com.example.library_management_be.repository.BookRepository;
import com.example.library_management_be.repository.FineRepository;
import com.example.library_management_be.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookLoanService {
    private final BookLoanRepository bookLoanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookLoanMapper bookLoanMapper;
    private final FineRepository fineRepository;

    public BookLoanService(BookLoanRepository bookLoanRepository, UserRepository userRepository, BookRepository bookRepository, BookLoanMapper bookLoanMapper, FineRepository fineRepository) {
        this.bookLoanRepository = bookLoanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookLoanMapper = bookLoanMapper;
        this.fineRepository = fineRepository;
    }

    // Add methods to handle book loan operations, such as borrowing and returning books
    @Transactional
    public BaseResponse<?> borrowBook(Authentication authentication, BookLoanRequest request) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException("User not found"));

        LocalDate borrowDate = LocalDate.now();

        List<BookLoanResponse> successfulLoans = new ArrayList<>();
        List<String> failedBooks = new ArrayList<>();

        for (Long bookId : request.getBookIds()) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookException.BookNotFoundException("Book not found with ID: " + bookId));

            if (book.getAvailableQuantity() > 0) {
                book.setAvailableQuantity(book.getAvailableQuantity() - 1);
                bookRepository.save(book);

                BookLoan bookLoan = new BookLoan();
                bookLoan.setUser(user);
                bookLoan.setBook(book);
                bookLoan.setBorrowDate(borrowDate);
                bookLoan.setDueDate(request.getDueDate());
                bookLoan.setBook_condition(EBookCondition.GOOD);
                bookLoan.setStatus(ELoanStatus.BORROWED);
                bookLoanRepository.save(bookLoan);

                BookLoanResponse loanResponse = bookLoanMapper.toResponse(bookLoan);
                successfulLoans.add(loanResponse);
            } else {
                failedBooks.add(book.getTitle());
            }
        }

        if (!successfulLoans.isEmpty()) {
            return BaseResponse.builder()
                    .status("success")
                    .message("Some or all books borrowed successfully")
                    .data(successfulLoans)
                    .build();
        } else {
            return BaseResponse.builder()
                    .status("fail")
                    .message("No books were borrowed. Out of stock: " + String.join(", ", failedBooks))
                    .build();
        }
    }

    @Transactional
    public BaseResponse<Void> returnBook(Long loanId, BookReturnRequest request) {
        BookLoan loan = bookLoanRepository.findById(loanId)
                .orElseThrow(() -> new BookException.BookNotFoundException("Không tìm thấy thông tin mượn sách"));

        if (loan.getStatus() == ELoanStatus.RETURNED) {
            throw new BookException.BookAlreadyExistsException("Sách đã được trả trước đó");
        }

        LocalDate now = LocalDate.now();
        Book book = loan.getBook();

        // Cập nhật thông tin trả sách
        loan.setReturnDate(now);
        loan.setBook_condition(request.getBookCondition());
        loan.setStatus(ELoanStatus.RETURNED);

        // Cập nhật số lượng sách
        switch (request.getBookCondition()) {
            case LOST:
                book.setTotalQuantity(book.getTotalQuantity() - 1);
                break;
            case DAMAGED:
            case GOOD:
                book.setAvailableQuantity(book.getAvailableQuantity() + 1);
                break;
            default:
                throw new IllegalArgumentException("Invalid book condition: " + request.getBookCondition());
        }

        if (book.getAvailableQuantity() > book.getTotalQuantity()) {
            throw new RuntimeException("Lỗi dữ liệu: Số lượng có sẵn không thể lớn hơn tổng số lượng");
        }
        if (book.getTotalQuantity() < 0) {
            throw new RuntimeException("Lỗi: Tổng số lượng sách không thể âm");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 1. Phạt vì trễ hạn (giới hạn 150.000 VNĐ)
        if (loan.getDueDate().isBefore(now)) {
            long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), now);
            BigDecimal overdueAmount = BigDecimal.valueOf(5000L * daysLate);
            if (overdueAmount.compareTo(BigDecimal.valueOf(150000)) > 0) {
                overdueAmount = BigDecimal.valueOf(150000);
            }
            totalAmount = totalAmount.add(overdueAmount);

            Fine fine = new Fine()
                    .setAmount(overdueAmount)
                    .setReason(EFineReason.OVERDUE)
                    .setStatus(EFineStatus.PENDING)
                    .setIssuedDate(LocalDateTime.now())
                    .setUser(loan.getUser())
                    .setBookLoan(loan);
            fineRepository.save(fine);
        }

        // 2. Phạt vì mất sách
        if (request.getBookCondition() == EBookCondition.LOST) {
            BigDecimal lostAmount = BigDecimal.valueOf(200000);
            totalAmount = totalAmount.add(lostAmount);

            Fine fine = new Fine()
                    .setAmount(lostAmount)
                    .setReason(EFineReason.LOST)
                    .setStatus(EFineStatus.PENDING)
                    .setIssuedDate(LocalDateTime.now())
                    .setUser(loan.getUser())
                    .setBookLoan(loan);
            fineRepository.save(fine);
        }

        // 3. Phạt vì hư hỏng
        if (request.getBookCondition() == EBookCondition.DAMAGED) {
            BigDecimal damageAmount = BigDecimal.valueOf(50000);
            totalAmount = totalAmount.add(damageAmount);

            Fine fine = new Fine()
                    .setAmount(damageAmount)
                    .setReason(EFineReason.DAMAGED)
                    .setStatus(EFineStatus.PENDING)
                    .setIssuedDate(LocalDateTime.now())
                    .setUser(loan.getUser())
                    .setBookLoan(loan);
            fineRepository.save(fine);
        }

        // Lưu thay đổi
        bookRepository.save(book);
        bookLoanRepository.save(loan);

        return BaseResponse.<Void>builder()
                .status("success")
                .message("Sách đã được trả thành công" +
                        (totalAmount.compareTo(BigDecimal.ZERO) > 0
                                ? " với tổng phạt: " + totalAmount + " VNĐ" : ""))
                .data(null)
                .build();
    }

    public BaseResponse<Page<GetAllBookLoanResponse>> getAllLoans(BookLoanFilterRequest filter, Pageable pageable) {
        Specification<BookLoan> spec = (root, query, cb) -> cb.conjunction();

        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("user").get("email")), "%" + filter.getEmail().toLowerCase() + "%"));
        }

        if (filter.getFullName() != null && !filter.getFullName().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("user").get("fullName")), "%" + filter.getFullName().toLowerCase() + "%"));
        }

        if (filter.getBookTitle() != null && !filter.getBookTitle().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("book").get("title")), "%" + filter.getBookTitle().toLowerCase() + "%"));
        }

        // ✅ Sửa đúng tên field "bookCondition"
        if (filter.getBookCondition() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("book_condition"), filter.getBookCondition()));
        }

        if (filter.getStatus() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), filter.getStatus()));
        }

        if (filter.getFromDate() != null && filter.getToDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.between(root.get("borrowDate"), filter.getFromDate(), filter.getToDate()));
        } else if (filter.getFromDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("borrowDate"), filter.getFromDate()));
        } else if (filter.getToDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("borrowDate"), filter.getToDate()));
        }

        Page<BookLoan> pageResult = bookLoanRepository.findAll(spec, pageable);

        Page<GetAllBookLoanResponse> response = pageResult.map(BookLoanMapper::GetAllToResponse);

        return BaseResponse.<Page<GetAllBookLoanResponse>>builder()
                .status("success")
                .message("Danh sách đơn mượn đã lọc")
                .data(response)
                .build();
    }

    public List<GetAllBookLoanResponse> getLoansByUserEmail(String email) {
        List<BookLoan> loans = bookLoanRepository.findByUserEmail(email);

        return loans.stream()
                .map(BookLoanMapper::GetAllToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BaseResponse<?> extendBookLoan(Long id, ExtendBookRequest request) {
        BookLoan bookLoan = bookLoanRepository.findById(id)
                .orElseThrow(() -> new BookException.BookNotFoundException("Không tìm thấy thông tin mượn sách"));

        if (bookLoan.getStatus() == ELoanStatus.RETURNED) {
            throw new BookException.BookAlreadyExistsException("Sách đã được trả trước đó");
        }

        LocalDate currentDueDate = bookLoan.getDueDate();
        LocalDate newDueDate = request.getNewDueDate();

        if (newDueDate == null) {
            throw new IllegalArgumentException("Ngày hạn trả mới không được để trống");
        }

        if (newDueDate.isBefore(bookLoan.getBorrowDate())) {
            throw new IllegalArgumentException("Ngày hạn trả mới không được trước ngày mượn sách");
        }

        if (!newDueDate.isAfter(currentDueDate)) {
            throw new IllegalArgumentException("Ngày hạn trả mới phải sau ngày hạn hiện tại");
        }

        if (newDueDate.isAfter(LocalDate.now().plusDays(60))) {
            throw new IllegalArgumentException("Không thể gia hạn quá 60 ngày kể từ hôm nay");
        }

        bookLoan.setDueDate(newDueDate);
        bookLoanRepository.save(bookLoan);

        return BaseResponse.builder()
                .status("success")
                .message("Gia hạn mượn sách thành công")
                .data(bookLoanMapper.toResponse(bookLoan))
                .build();
    }

    @Transactional
    public void deleteBookLoan(Long id) {
        BookLoan bookLoan = bookLoanRepository.findById(id)
                .orElseThrow(() -> new BookException.BookNotFoundException("Không tìm thấy thông tin mượn sách"));

        // Xóa các phiếu phạt liên quan
        List<Fine> fines = fineRepository.findByBookLoan(bookLoan);
        fineRepository.deleteAll(fines);
        bookLoanRepository.delete(bookLoan);
    }

}
