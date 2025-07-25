package com.example.library_management_be.exception;

import com.example.library_management_be.dto.BaseRespone;
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
    private ResponseEntity<BaseRespone<Object>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(BaseRespone.builder()
                .status("error")
                .message(message)
                .data(null)
                .build());
    }

    // --- 1. Validation errors ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseRespone<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        String message = errors.values().stream().findFirst().orElse("Validation failed");
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    // --- 2. User-related custom exceptions ---
    @ExceptionHandler({
            UserException.UserNotVerifiedException.class,
            UserException.UserNotActiveException.class,
            UserException.InvalidTokenException.class,
            UserException.UserAlreadyVerifiedException.class
    })
    public ResponseEntity<BaseRespone<Object>> handleUserExceptions(RuntimeException ex) {
        HttpStatus status;
        if (ex instanceof UserException.UserNotVerifiedException || ex instanceof UserException.UserNotActiveException) {
            status = HttpStatus.FORBIDDEN;
        } else if (ex instanceof UserException.InvalidTokenException) {
            status = HttpStatus.UNAUTHORIZED;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return buildErrorResponse(status, ex.getMessage());
    }

    // --- 3. Conflict: Email đã tồn tại / Mật khẩu yếu ---
    @ExceptionHandler({
            UserException.UserAlreadyExistsException.class,
            UserException.WeakPasswordException.class
    })
    public ResponseEntity<BaseRespone<Object>> handleUserConflict(UserException ex) {
        HttpStatus status = (ex instanceof UserException.UserAlreadyExistsException)
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;
        return buildErrorResponse(status, ex.getMessage());
    }

    // --- 4. Sai email hoặc password ---
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseRespone<Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    // --- 5. Không tìm thấy user theo custom exception ---
    @ExceptionHandler({
            UserException.UserNotFoundException.class,
            CategoryException.CategoryNotFoundException.class
    })
    public ResponseEntity<BaseRespone<Object>> handleNotFoundExceptions(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // --- 6. Không tìm thấy user theo Spring Security ---
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<BaseRespone<Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Email không tồn tại");
    }

    // --- 7. Fallback cho các lỗi không xác định ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseRespone<Object>> handleAllExceptions(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
