package com.example.library_management_be.entity;

import com.example.library_management_be.entity.enums.EBookCondition;
import com.example.library_management_be.entity.enums.ELoanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BookLoan extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate; // Ngày mượn sách

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate; // Ngày trả sách dự kiến

    @Column(name = "return_date")
    private LocalDate returnDate; // Ngày trả sách, có thể null nếu chưa trả

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ELoanStatus status; // Trạng thái mượn sách, ví dụ: "borrowed", "returned", "overdue"

    @Column(name = "book_condition", nullable = false)
    @Enumerated(EnumType.STRING)
    private EBookCondition book_condition; // Trạng thái của sách khi mượn, ví dụ: "lost", "good", "damaged"

    @OneToMany(mappedBy = "bookLoan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fine> fines = new ArrayList<>();



}
