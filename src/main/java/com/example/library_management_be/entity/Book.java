package com.example.library_management_be.entity;

import com.example.library_management_be.entity.enums.EBookStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Book extends BaseEntity{
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    //Mã sách (ISBN) là duy nhất cho mỗi cuốn sách
    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    private String publisher;
    private int publicationYear;

    @Column(columnDefinition = "TEXT")
    private String description;
    // Thể loại sách
    private String genre;

    // Số lượng sách có sẵn và tổng số lượng sách
    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    // Vị trí của sách trong thư viện (ví dụ: kệ, phòng)
    private String location;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EBookStatus status; // e.g., "available", "checked out", "reserved"

    private Double averageRating; // Optional field for average rating
    private int totalRatings; // Optional field for total number of ratings
    private String imageUrl;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<BookLoan> bookLoans;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_category",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories; // Danh sách danh mục mà sách này thuộc về

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations; // Danh sách đặt sách của người dùng

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks; // Danh sách phản hồi của người dùng về sách này

}
