package com.app.kh.camnextgen.modules.user.dto;

public record UserSummaryResponse(
        Long id,
        String email,
        String fullName,
        String status
) {
}
