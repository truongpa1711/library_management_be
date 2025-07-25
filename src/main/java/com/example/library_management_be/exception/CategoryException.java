package com.example.library_management_be.exception;

public class CategoryException extends RuntimeException {
    public CategoryException(String message) {
        super(message);
    }

    public static class CategoryNotFoundException extends RuntimeException {
        public CategoryNotFoundException(String message) {
            super(message);
        }
    }

    public static class CategoryAlreadyExistsException extends RuntimeException {
        public CategoryAlreadyExistsException(String message) {
            super(message);
        }
    }
}
