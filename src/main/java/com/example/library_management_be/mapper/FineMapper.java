package com.example.library_management_be.mapper;

import com.example.library_management_be.dto.response.FineResponse;
import com.example.library_management_be.entity.Fine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FineMapper {

    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "bookLoan.id", target = "bookLoanId")
    FineResponse toResponse(Fine fine);
}
