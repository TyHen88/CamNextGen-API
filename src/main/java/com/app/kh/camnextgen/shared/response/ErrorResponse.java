package com.app.kh.camnextgen.shared.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        List<FieldError> errors,
        Instant timestamp,
        String requestId
) {
    public record FieldError(String field, String message) {}

    public static ErrorResponse of(String code, String message, String requestId) {
        return new ErrorResponse(code, message, List.of(), Instant.now(), requestId);
    }
}
