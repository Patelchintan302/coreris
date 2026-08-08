package com.example.coreris.exception_handler;


import com.example.coreris.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            PatientNotFoundException.class,
            AppointmentNotFoundException.class,
            UserNotFoundException.class,
            ScanResultNotFoundException.class,
            ReportNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDto> handleNotFoundExceptions(RuntimeException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(request.getRequestURI())
                .errors(validationErrors)
                .build();
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred on the server.")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponseDto,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("User Don't have permission to perform the action")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponseDto,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponseDto,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponseDto> handleFileStorageException(FileStorageException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Default to 500

        String message = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        // 1. If file does not exist, return 404 Not Found
        if (message.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        }
        // 2. If it's a security/traversal bypass attempt, return 400 Bad Request
        else if (message.contains("invalid path") || message.contains("invalid filename")) {
            status = HttpStatus.BAD_REQUEST;
        }

        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponseDto, status);
    }

}
