package com.example.library_management_be.repository;

import com.example.library_management_be.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>, JpaSpecificationExecutor<Feedback> {
    List<Feedback> findByBook_Id(Long bookId);
    List<Feedback> findByUser_Id(Long userId);
}