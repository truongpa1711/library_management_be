package com.example.library_management_be.service;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.FeedbackRequest;
import com.example.library_management_be.dto.request.FeedbackStatusRequest;
import com.example.library_management_be.dto.request.FeedbackUpdateRequest;
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
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public BaseResponse<Page<FeedbackResponse>> getFeedbacksByBookId(Long bookId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Feedback> feedbackPage = feedbackRepository.findByBookId(bookId, pageable);

        Page<FeedbackResponse> responsePage = feedbackPage.map(feedbackMapper::toDto);

        return BaseResponse.<Page<FeedbackResponse>>builder()
                .status("success")
                .message("Feedbacks retrieved successfully")
                .data(responsePage)
                .build();
    }

    @Transactional
    public BaseResponse<FeedbackResponse> replyFeedback(Long id, String replyContent) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new BaseException.CustomNotFoundException("Feedback không tồn tại"));

        if (feedback.getStatus() == EFeedbackStatus.REJECTED) {
            throw new BaseException.CustomBadRequestException("Feedback đã bị từ chối");
        }

        feedback.setReply(
                (replyContent == null || replyContent.trim().isEmpty())
                        ? "Đã duyệt phản hồi"
                        : replyContent
        );
        feedback.setRepliedDate(LocalDateTime.now());
        feedback.setStatus(EFeedbackStatus.APPROVED);
        feedback.setIsEditable(false); // Không cho phép người dùng chỉnh sửa sau khi đã trả lời

        // === Cập nhật rating cho sách ===
        Book book = feedback.getBook();
        double averageRating = book.getAverageRating() != null ? book.getAverageRating() : 0.0;
        Integer totalRatingsObj = book.getTotalRatings();
        int totalRatings = totalRatingsObj != null ? totalRatingsObj : 0;

        double newAverage = (averageRating * totalRatings + feedback.getRating()) / (totalRatings + 1);
        book.setAverageRating(newAverage);
        book.setTotalRatings(totalRatings + 1);
        bookRepository.save(book);

        // === Lưu lại feedback sau khi xử lý ===
        feedbackRepository.save(feedback);
        FeedbackResponse response = feedbackMapper.toDto(feedback);

        return BaseResponse.<FeedbackResponse>builder()
                .status("success")
                .message("Feedback replied successfully")
                .data(response)
                .build();
    }


    @Transactional
    public BaseResponse<FeedbackResponse> updateFeedbackStatus(Long id, FeedbackStatusRequest request) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new BaseException.CustomNotFoundException("Feedback không tồn tại"));

        if (feedback.getStatus() != EFeedbackStatus.PENDING) {
            throw new BaseException.CustomBadRequestException("Feedback đã được xử lý trước đó");
        }

        Book book = feedback.getBook();
        if (book == null) {
            throw new BaseException.CustomNotFoundException("Book không tồn tại");
        }

        feedback.setStatus(request.getStatus());
        feedback.setIsEditable(false); // Không cho phép chỉnh sửa sau xử lý

        if (request.getStatus() == EFeedbackStatus.APPROVED) {
            feedback.setRepliedDate(LocalDateTime.now());

            // Cập nhật rating nếu được duyệt
            double averageRating = book.getAverageRating() != null ? book.getAverageRating() : 0.0;
            Integer totalRatingsObj = book.getTotalRatings();
            int totalRatings = totalRatingsObj != null ? totalRatingsObj : 0;

            double newAverage = (averageRating * totalRatings + feedback.getRating()) / (totalRatings + 1);
            book.setAverageRating(newAverage);
            book.setTotalRatings(totalRatings + 1);

            bookRepository.save(book);
        }


        feedbackRepository.save(feedback);

        FeedbackResponse response = feedbackMapper.toDto(feedback);
        return BaseResponse.<FeedbackResponse>builder()
                .status("success")
                .message("Cập nhật trạng thái phản hồi thành công")
                .data(response)
                .build();
    }


    @Transactional
    public BaseResponse<FeedbackResponse> updateFeedback(Long id, FeedbackUpdateRequest request, Authentication auth) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new BaseException.CustomNotFoundException("Feedback không tồn tại"));

        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserException.UserNotFoundException("User not found"));
        if (!feedback.getUser().getId().equals(currentUser.getId())) {
            throw new BaseException.CustomBadRequestException("Bạn không có quyền sửa feedback này");
        }

        // Kiểm tra trạng thái isEditable
        if (!feedback.getIsEditable()) {
            throw new BaseException.CustomBadRequestException("Feedback này không thể chỉnh sửa nữa");
        }

        // Cập nhật nội dung feedback
        feedback.setContent(request.getContent());
        feedback.setRating(request.getRating());
        Feedback updated = feedbackRepository.save(feedback);
        FeedbackResponse response = feedbackMapper.toDto(updated);
        return BaseResponse.<FeedbackResponse>builder()
                .status("success")
                .message("Feedback updated successfully")
                .data(response)
                .build();
    }

    public void deleteFeedback(Long id, Authentication auth) {
        String email = auth.getName();
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new BaseException.CustomNotFoundException("Feedback không tồn tại"));

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException("User không tồn tại"));
        if (!feedback.getUser().getId().equals(currentUser.getId())) {
            throw new BaseException.CustomBadRequestException("Bạn không có quyền xóa feedback này");
        }

        if (!feedback.getIsEditable()) {
            throw new BaseException.CustomBadRequestException("Feedback không thể xóa vì đã được duyệt hoặc từ chối");
        }

        feedbackRepository.delete(feedback);
    }

    public BaseResponse<Page<FeedbackResponse>> getAllFeedbacks(String status, String bookTitle, String userEmail, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        EFeedbackStatus enumStatus = null;
        if (status != null) {
            try {
                enumStatus = EFeedbackStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BaseException.CustomBadRequestException("Trạng thái không hợp lệ: " + status);
            }
        }

        Page<Feedback> feedbacks = feedbackRepository.findByFilters(enumStatus, bookTitle, userEmail, pageable);
        Page<FeedbackResponse> responsePage = feedbacks.map(feedbackMapper::toDto);
        return new BaseResponse<>("success", "Danh sách feedback đã lọc", responsePage);
    }



}
