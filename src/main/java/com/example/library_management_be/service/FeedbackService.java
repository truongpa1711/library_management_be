package com.example.library_management_be.service;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.FeedbackRequest;
import com.example.library_management_be.dto.response.FeedbackResponse;
import com.example.library_management_be.entity.Book;
import com.example.library_management_be.entity.Feedback;
import com.example.library_management_be.entity.User;
import com.example.library_management_be.entity.enums.EFeedbackStatus;
import com.example.library_management_be.exception.BaseException;
import com.example.library_management_be.exception.BookException;
import com.example.library_management_be.exception.UserException;
import com.example.library_management_be.mapper.FeedbackMapper;
import com.example.library_management_be.repository.BookLoanRepository;
import com.example.library_management_be.repository.BookRepository;
import com.example.library_management_be.repository.FeedbackRepository;
import com.example.library_management_be.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookLoanRepository bookLoanRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, FeedbackMapper feedbackMapper, UserRepository userRepository, BookRepository bookRepository, BookLoanRepository bookLoanRepository) {
        this.feedbackRepository = feedbackRepository;
        this.feedbackMapper = feedbackMapper;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookLoanRepository = bookLoanRepository;
    }

    public BaseResponse<FeedbackResponse> createFeedback(FeedbackRequest dto, Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException("User not found"));
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new BookException.BookNotFoundException("Book not found"));


        boolean hasBorrowed = bookLoanRepository.existsByUserAndBook(user, book);
        if (!hasBorrowed) {
            throw new BaseException.CustomBadRequestException("Bạn phải mượn sách này trước khi đánh giá");
        }

        // Kiểm tra đã feedback chưa (1 user chỉ feedback 1 lần/sách?)
        boolean alreadyFeedbacked = feedbackRepository.findByBook_Id(book.getId()).stream()
                .anyMatch(f -> f.getUser().getId().equals(user.getId()));
        if (alreadyFeedbacked) {
            throw new BaseException.CustomBadRequestException("Bạn đã đánh giá sách này rồi");
        }

        Feedback feedback = new Feedback()
                .setUser(user)
                .setBook(book)
                .setContent(dto.getContent())
                .setRating(dto.getRating())
                .setStatus(EFeedbackStatus.PENDING)
                .setIsEditable(true)
                .setCreatedDate(LocalDateTime.now());

        feedbackRepository.save(feedback);

        return BaseResponse.<FeedbackResponse>builder()
                .status("success")
                .message("Feedback created successfully")
                .data(feedbackMapper.toDto(feedback))
                .build();


    }


}
