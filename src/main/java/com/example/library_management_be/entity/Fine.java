package com.example.library_management_be.entity;

import com.example.library_management_be.entity.enums.EFineReason;
import com.example.library_management_be.entity.enums.EFineStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Fine extends BaseEntity{
    //Ví dụ phạt 5.000 VNĐ/ngày trễ, nếu trả sách trễ 3 ngày → amount = 15000.
    @Column(nullable = false)
    private BigDecimal amount; // VD: 5000 VNĐ/ngày

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EFineReason reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EFineStatus status = EFineStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime issuedDate;

    private LocalDateTime paidDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_loan_id")
    private BookLoan bookLoan;
}
