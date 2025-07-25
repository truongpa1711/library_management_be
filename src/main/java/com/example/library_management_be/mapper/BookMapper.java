package com.example.library_management_be.mapper;

import com.example.library_management_be.dto.request.BookRequest;
import com.example.library_management_be.entity.Book;
import com.example.library_management_be.entity.Category;
import com.example.library_management_be.dto.response.BookResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface BookMapper {

    @Mapping(source = "categories", target = "categories", qualifiedByName = "mapCategories")
    BookResponse toDto(Book book);

    @Named("mapCategories")
    default List<BookResponse.CategoryDto> mapCategories(List<Category> categories) {
        return categories.stream().map(category -> {
            BookResponse.CategoryDto dto = new BookResponse.CategoryDto();
            dto.setId(category.getId());
            dto.setName(category.getName());
            return dto;
        }).toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "status", ignore = true)
    Book toEntity(BookRequest bookRequest);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "categories", ignore = true)
    void updateEntityFromRequest(
            BookRequest bookRequest, @MappingTarget Book book
    );
}
