package com.app.kh.camnextgen.shared.api;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data,
        Instant timestamp,
        String requestId
) {
    public static <T> ApiResponse<T> ok(T data, String requestId) {
        return new ApiResponse<>(true, "OK", "success", data, Instant.now(), requestId);
    }

    public static <T> ApiResponse<T> error(String code, String message, String requestId) {
        return new ApiResponse<>(false, code, message, null, Instant.now(), requestId);
    }
}
