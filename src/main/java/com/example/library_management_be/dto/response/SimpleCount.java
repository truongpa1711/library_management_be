package com.example.library_management_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleCount {
    private String name;
    private long count;
}
