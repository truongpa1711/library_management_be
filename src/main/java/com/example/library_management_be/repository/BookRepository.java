package com.example.library_management_be.repository;

import com.example.library_management_be.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitle(String title); // Find book by title

}
