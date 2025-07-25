package com.example.library_management_be.exception;

import com.example.library_management_be.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- Helper method ---
    private ResponseEntity<BaseResponse<Object>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(BaseResponse.builder()
                .status("error")
                .message(message)
                .data(null)
                .build());
    }

    // --- 1. Validation errors ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        String message = errors.values().stream().findFirst().orElse("Validation failed");
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    // --- 2. 403 FORBIDDEN ---
    @ExceptionHandler({
            UserException.UserNotVerifiedException.class,
            UserException.UserNotActiveException.class
    })
    public ResponseEntity<BaseResponse<Object>> handleForbidden(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // --- 3. 401 UNAUTHORIZED ---
    @ExceptionHandler({
            UserException.InvalidTokenException.class,
            BadCredentialsException.class
    })
    public ResponseEntity<BaseResponse<Object>> handleUnauthorized(RuntimeException ex) {
        String message = (ex instanceof BadCredentialsException) ? "Invalid email or password" : ex.getMessage();
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, message);
    }

    // --- 4. 400 BAD REQUEST ---
    @ExceptionHandler({
            UserException.UserAlreadyVerifiedException.class,
            UserException.WeakPasswordException.class,
            BookException.InvalidBookStatusException.class,
    })
    public ResponseEntity<BaseResponse<Object>> handleBadRequest(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // --- 5. 409 CONFLICT ---
    @ExceptionHandler({
            UserException.UserAlreadyExistsException.class,
            BookException.BookAlreadyExistsException.class,
            CategoryException.CategoryAlreadyExistsException.class
    })
    public ResponseEntity<BaseResponse<Object>> handleConflict(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // --- 6. 404 NOT FOUND ---
    @ExceptionHandler({
            UserException.UserNotFoundException.class,
            CategoryException.CategoryNotFoundException.class,
            UsernameNotFoundException.class,
            BookException.BookNotFoundException.class,
            BookException.BookLoanNotFoundException.class
    })
    public ResponseEntity<BaseResponse<Object>> handleNotFound(RuntimeException ex) {
        String message = (ex instanceof UsernameNotFoundException) ? "Email không tồn tại" : ex.getMessage();
        return buildErrorResponse(HttpStatus.NOT_FOUND, message);
    }

    // --- 7. 500 INTERNAL SERVER ERROR ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleAllExceptions(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
