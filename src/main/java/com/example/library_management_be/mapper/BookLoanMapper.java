package com.example.library_management_be.mapper;

import com.example.library_management_be.dto.BookLoanResponse;
import com.example.library_management_be.dto.response.GetAllBookLoanResponse;
import com.example.library_management_be.entity.BookLoan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // dùng @Autowired được
public interface BookLoanMapper {

    BookLoanMapper INSTANCE = Mappers.getMapper(BookLoanMapper.class);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "book_condition", target = "condition") // enum to String
    @Mapping(source = "status", target = "status")             // enum to String
    BookLoanResponse toResponse(BookLoan loan);

    public static GetAllBookLoanResponse GetAllToResponse(BookLoan loan) {
        return GetAllBookLoanResponse.builder()
                .loanId(loan.getId())
                .userFullName(loan.getUser().getFullName())
                .userEmail(loan.getUser().getEmail())
                .userId(loan.getUser().getId())
                .bookId(loan.getBook().getId())
                .bookAuthor(loan.getBook().getAuthor())
                .bookGenre(loan.getBook().getGenre())
                .bookTitle(loan.getBook().getTitle())
                .borrowDate(loan.getBorrowDate())
                .dueDate(loan.getDueDate())
                .returnDate(loan.getReturnDate())
                .bookCondition(loan.getBook_condition())
                .status(loan.getStatus())
                .build();
    }
}
