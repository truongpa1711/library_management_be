package com.example.library_management_be.repository;

import com.example.library_management_be.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitle(String title); // Find book by title

    Page<Book> findAll(Specification<Book> spec, Pageable pageable);

    List<Book> findTop5ByOrderByBorrowCountDesc();

    // Cho phép giới hạn linh hoạt:
    @Query("SELECT b FROM Book b ORDER BY b.borrowCount DESC")
    List<Book> findTopBorrowed(Pageable pageable);

    @Query("""
    SELECT b FROM Book b 
    WHERE b.id <> :bookId AND (
        (b.genre = :genre AND b.author = :author) OR 
        (b.genre = :genre OR b.author = :author)
    )
    ORDER BY 
        CASE 
            WHEN b.genre = :genre AND b.author = :author THEN 0
            WHEN b.genre = :genre OR b.author = :author THEN 1
            ELSE 2
        END,
        b.borrowCount DESC
""")
    List<Book> findSimilarBooks(
            @Param("bookId") Long bookId,
            @Param("genre") String genre,
            @Param("author") String author,
            Pageable pageable
    );

    @Query("SELECT COUNT(b) FROM Book b")
    long countTotalBooks();

    @Query("SELECT SUM(b.borrowCount) FROM Book b")
    Long countTotalBorrowed();

    @Query("""
    SELECT b.author, COUNT(b) 
    FROM Book b 
    GROUP BY b.author 
    ORDER BY COUNT(b) DESC
""")
    List<Object[]> findTopAuthors(Pageable pageable);

    @Query("""
    SELECT b.genre, COUNT(b) 
    FROM Book b 
    GROUP BY b.genre 
    ORDER BY COUNT(b) DESC
""")
    List<Object[]> findTopGenres(Pageable pageable);



}
