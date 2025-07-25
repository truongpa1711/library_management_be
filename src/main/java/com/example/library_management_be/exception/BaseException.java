package com.example.library_management_be.exception;

public class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }

    public static class CustomNotFoundException extends RuntimeException {
        public CustomNotFoundException(String message) {
            super(message);
        }
    }
    public static class CustomBadRequestException extends RuntimeException {
        public CustomBadRequestException(String message) {
            super(message);
        }
    }
}
