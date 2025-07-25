package com.example.library_management_be.exception;

public class BookException extends RuntimeException {
    public BookException(String message) {
        super(message);
    }

  public static class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String message) {
      super(message);
    }
  }

  public static class InvalidBookStatusException extends RuntimeException {
    public InvalidBookStatusException(String message) {
      super(message);
    }
  }

    public static class BookNotFoundException extends RuntimeException {
        public BookNotFoundException(String message) {
        super(message);
        }
    }

    public static class BookLoanNotFoundException extends RuntimeException {
        public BookLoanNotFoundException(String message) {
            super(message);
        }
    }
}
