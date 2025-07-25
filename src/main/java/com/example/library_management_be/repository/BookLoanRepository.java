package com.example.library_management_be.repository;

import com.example.library_management_be.entity.BookLoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    Page<BookLoan> findAll(Specification<BookLoan> spec, Pageable pageable);

    List<BookLoan> findByUserEmail(String email);
    // Additional query methods can be defined here if needed
}
