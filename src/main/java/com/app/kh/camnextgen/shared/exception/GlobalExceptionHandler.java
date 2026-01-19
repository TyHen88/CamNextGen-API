package com.app.kh.camnextgen.shared.exception;

import com.app.kh.camnextgen.shared.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        return buildResponse(ex.getCode(), ex.getMessage(), List.of(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return buildResponse(ex.getCode(), ex.getMessage(), List.of(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(this::toFieldError)
            .collect(Collectors.toList());
        return buildResponse("VALIDATION_ERROR", "Validation failed", errors, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse("ACCESS_DENIED", "Access denied", List.of(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex, HttpServletRequest request) {
        return buildResponse("INTERNAL_ERROR", "Unexpected error", List.of(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ErrorResponse.FieldError toFieldError(FieldError error) {
        return new ErrorResponse.FieldError(error.getField(), error.getDefaultMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(String code, String message, List<ErrorResponse.FieldError> errors,
                                                       HttpStatus status, HttpServletRequest request) {
        String requestId = (String) request.getAttribute("correlationId");
        ErrorResponse response = new ErrorResponse(code, message, errors, Instant.now(), requestId);
        return ResponseEntity.status(status).body(response);
    }
}
