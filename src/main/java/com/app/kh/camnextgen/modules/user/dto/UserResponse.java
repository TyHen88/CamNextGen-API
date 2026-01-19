package com.app.kh.camnextgen.modules.user.dto;

import java.util.Set;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        String status,
        Set<String> roles
) {
}
