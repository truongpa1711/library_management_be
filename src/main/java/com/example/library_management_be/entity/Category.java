package com.example.library_management_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
public class Category extends BaseEntity {
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    private String description;

    //thẻ gắn với danh mục, có thể là từ khóa hoặc thẻ mô tả
    @Column(name = "tags")
    private String tags;

    @ManyToMany(mappedBy = "categories")
    private List<Book> books; // Danh sách sách thuộc danh mục này
}
