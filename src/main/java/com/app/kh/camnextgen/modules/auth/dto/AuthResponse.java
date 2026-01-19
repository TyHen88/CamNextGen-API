package com.app.kh.camnextgen.modules.auth.dto;

import com.app.kh.camnextgen.modules.user.dto.UserResponse;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserResponse user
) {
}
