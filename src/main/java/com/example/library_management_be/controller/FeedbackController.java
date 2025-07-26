package com.example.library_management_be.controller;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.FeedbackRequest;
import com.example.library_management_be.dto.request.FeedbackStatusRequest;
import com.example.library_management_be.dto.request.FeedbackUpdateRequest;
import com.example.library_management_be.dto.response.FeedbackResponse;
import com.example.library_management_be.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<FeedbackResponse>> createFeedback(@RequestBody @Valid FeedbackRequest dto, Authentication auth) {
        return ResponseEntity.ok(feedbackService.createFeedback(dto, auth));
    }

    // 2. Lấy tất cả feedback của một quyển sách
    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Page<FeedbackResponse>>> getFeedbacksByBookId(@PathVariable Long bookId,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByBookId(bookId, page, size));
    }

    // 3. Admin trả lời feedback
    @PutMapping("/{id}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<FeedbackResponse>> replyFeedback(
            @PathVariable Long id,
            @RequestParam(required = false) String replyContent) {
        return ResponseEntity.ok(feedbackService.replyFeedback(id, replyContent));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<FeedbackResponse>> updateFeedbackStatus(
            @PathVariable Long id,
            @RequestBody @Valid FeedbackStatusRequest request) {
        System.out.println(request);
        return ResponseEntity.ok(feedbackService.updateFeedbackStatus(id, request));
    }

    // 4. User cập nhật feedback của chính mình
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<FeedbackResponse>> updateFeedback(
            @PathVariable Long id,
            @RequestBody @Valid FeedbackUpdateRequest dto,
            Authentication auth) {
        return ResponseEntity.ok(feedbackService.updateFeedback(id, dto, auth));
    }

    // 5. User xóa feedback của chính mình
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<Void>> deleteFeedback(@PathVariable Long id, Authentication auth) {
        feedbackService.deleteFeedback(id, auth);
        return ResponseEntity.ok(new BaseResponse<>("success", "Xóa feedback thành công", null));
    }

    // 6. Admin xem tất cả feedback
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Page<FeedbackResponse>>> getAllFeedbacks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String bookTitle,
            @RequestParam(required = false) String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(feedbackService.getAllFeedbacks(status, bookTitle, userEmail, page, size));
    }
}

