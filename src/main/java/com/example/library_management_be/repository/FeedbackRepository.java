package com.example.library_management_be.repository;

import com.example.library_management_be.entity.Book;
import com.example.library_management_be.entity.Feedback;
import com.example.library_management_be.entity.enums.EFeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>, JpaSpecificationExecutor<Feedback> {
    List<Feedback> findByBook_Id(Long bookId);
    List<Feedback> findByUser_Id(Long userId);


    Page<Feedback> findByBookId(Long bookId, Pageable pageable);

    @Query("""
    SELECT f FROM Feedback f
    WHERE (:status IS NULL OR f.status = :status)
      AND (:bookTitle IS NULL OR LOWER(f.book.title) LIKE LOWER(CONCAT('%', :bookTitle, '%')))
      AND (:userEmail IS NULL OR LOWER(f.user.email) LIKE LOWER(CONCAT('%', :userEmail, '%')))
""")
    Page<Feedback> findByFilters(@Param("status") EFeedbackStatus status,
                                 @Param("bookTitle") String bookTitle,
                                 @Param("userEmail") String userEmail,
                                 Pageable pageable);

}