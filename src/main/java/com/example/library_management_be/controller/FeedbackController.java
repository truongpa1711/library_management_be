package com.example.library_management_be.controller;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.FeedbackRequest;
import com.example.library_management_be.dto.response.FeedbackResponse;
import com.example.library_management_be.service.FeedbackService;
import jakarta.validation.Valid;
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

//    @PostMapping("/admin")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<BaseResponse<FeedbackResponse>> createFeedbackByAdmin(@RequestBody @Valid FeedbackRequest dto, Authentication auth) {
//        return ResponseEntity.ok(feedbackService.createFeedbackByAdmin(dto, auth));
//    }

//    @GetMapping("/book/{bookId}")
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
//    public ResponseEntity<BaseResponse<FeedbackResponse>> getFeedbackByBookId(@PathVariable Long bookId, Authentication auth) {
//        return ResponseEntity.ok(feedbackService.getFeedbackByBookId(bookId, auth));
//    }
    /*📌 2. Lấy tất cả feedback của một quyển sách
h
Sao chép
Chỉnh sửa
GET /api/feedbacks/book/{bookId}
Trả về list FeedbackResponse

Dùng để hiển thị khi user ấn vào một quyển sách

Có thể thêm phân trang:

http
Sao chép
Chỉnh sửa
GET /api/feedbacks/book/{bookId}?page=0&size=10
📌 3. Admin trả lời feedback
http
Sao chép
Chỉnh sửa
PATCH /api/feedbacks/{id}/reply
Body: { "reply": "Cảm ơn bạn đã góp ý!" }

Role: ADMIN

Cập nhật reply, repliedDate, status = RESOLVED

📌 4. User cập nhật feedback của chính mình
h
Sao chép
Chỉnh sửa
PUT /api/feedbacks/{id}
Yêu cầu: giống FeedbackRequest

Kiểm tra isEditable == true và user == auth.getName()

Role: USER

📌 5. User xóa feedback của chính mình
http
Sao chép
Chỉnh sửa
DELETE /api/feedbacks/{id}
Role: USER

Chỉ cho phép xóa nếu isEditable == true và user == auth.getName()

📌 6. Admin xem tất cả feedback (quản trị)
http
Sao chép
Chỉnh sửa
GET /api/feedbacks
Có filter: status=PENDING, bookTitle, userEmail, etc.

Role: ADMIN

Phân trang, sắp xếp

📌 7. Thống kê (tuỳ chọn)
http
Sao chép
Chỉnh sửa
GET /api/feedbacks/statistics
Ví dụ: top 5 sách có nhiều feedback nhất, trung bình rating, số feedback theo trạng thái...*/
}
