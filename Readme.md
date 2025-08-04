Các chức năng chính:

User:
- Đăng ký, đăng nhập, đăng xuất
- Xác thực email, yêu cầu gửi lại email xác thực
- Quên mật khẩu
- Refesh token (có api chưa link fe)
- Get, update infor
- changePassword
- getMyLoans
- createFeedback, updateFeedback, deleteFeedback
- getMyFines
- createReservation, getUserReservations, cancelReservation

Admin:
- createBook, updateBook, deleteBook
- borrowBook,returnBook ghi nhận cho user
- getAllLoans
- extendBook(gia hạn thời gian mượn sách)
- deleteBookLoan(chưa link)
- CRUD category
- replyFeedback
- updateFeedbackStatus, getAllFeedbacks
- getAllFines, updateFineStatus deleteFine
- getAllReservationsForAdmin, updateReservationStatus

Api khác:
- getAllUsers
- searchUserByEmail
- updateUser(admin)
- getBookById
- getAllBooks
- getBooksByCategory
- getTopBorrowedBooks
- getSimilarBooks
- getStatistics
- getFeedbacksByBookId