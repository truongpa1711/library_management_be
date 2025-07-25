package com.example.library_management_be.exception;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
    public static class UserNotVerifiedException extends UserException {
        public UserNotVerifiedException(String message) {
            super(message);
        }
    }

    public static class UserNotActiveException extends UserException {
        public UserNotActiveException(String message) {
            super(message);
        }
    }

    public static class UserAlreadyVerifiedException extends UserException {
        public UserAlreadyVerifiedException(String email) {
            super("User already verified with email: " + email);
        }
    }

    public static class InvalidTokenException extends UserException {
        public InvalidTokenException(String token) {
            super("Invalid token: " + token);
        }
    }

    public static class UserAlreadyExistsException extends UserException {
        public UserAlreadyExistsException(String email) {
            super("User already exists with email: " + email);
        }
    }

    public static class WeakPasswordException extends UserException {
        public WeakPasswordException() {
            super("Password must be at least 8 characters long");
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}
