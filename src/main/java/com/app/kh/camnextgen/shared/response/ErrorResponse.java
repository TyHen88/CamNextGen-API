package com.app.kh.camnextgen.shared.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(String code, String message, List<FieldError> errors, Instant timestamp, String requestId) {
    public record FieldError(String field, String message) {}
}
