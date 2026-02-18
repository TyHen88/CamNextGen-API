package com.app.kh.camnextgen.auth.dto;

import com.app.kh.camnextgen.user.dto.UserResponse;

public record AuthResponse(String accessToken, String refreshToken) {}
