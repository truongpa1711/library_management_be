package com.example.library_management_be.repository;

import com.example.library_management_be.entity.BookLoan;
import com.example.library_management_be.entity.Fine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FineRepository extends JpaRepository<Fine, Long> {
    Page<Fine> findAll(Specification<Fine> spec, Pageable pageable);

    List<Fine> findByBookLoan(BookLoan bookLoan);
    // Additional query methods can be defined here if needed
}
