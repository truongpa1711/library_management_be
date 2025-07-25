package com.example.library_management_be.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    public String status; // "success" or "error"
    public String message; // Message to be returned to the client
    public T data; // Data to be returned, can be any type
}
