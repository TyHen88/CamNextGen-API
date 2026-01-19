package com.app.kh.camnextgen.shared.response;

import java.time.Instant;

public record ApiResponse<T>(boolean success, String code, String message, T data, Instant timestamp, String requestId) {
    public static <T> ApiResponse<T> ok(T data, String requestId) {
        return new ApiResponse<>(true, "OK", "Success", data, Instant.now(), requestId);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ok(data, null);
    }
}
