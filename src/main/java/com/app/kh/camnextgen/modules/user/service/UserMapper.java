package com.app.kh.camnextgen.modules.user.service;

import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.modules.user.dto.UserResponse;
import com.app.kh.camnextgen.modules.user.dto.UserSummaryResponse;
import java.util.Set;
import java.util.stream.Collectors;

public final class UserMapper {
    private UserMapper() {}

    public static UserResponse toUserResponse(User user) {
        Set<String> roles = user.getRoles().stream()
            .map(userRole -> userRole.getRole().getCode())
            .collect(Collectors.toSet());
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getStatus().name(), roles);
    }

    public static UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(user.getId(), user.getEmail(), user.getFullName(), user.getStatus().name());
    }
}
