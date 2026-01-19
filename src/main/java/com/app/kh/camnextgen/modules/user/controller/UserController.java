package com.app.kh.camnextgen.modules.user.controller;

import com.app.kh.camnextgen.modules.user.dto.UserResponse;
import com.app.kh.camnextgen.modules.user.dto.UserSummaryResponse;
import com.app.kh.camnextgen.modules.user.service.UserService;
import com.app.kh.camnextgen.shared.response.ApiResponse;
import com.app.kh.camnextgen.shared.response.PageResponse;
import com.app.kh.camnextgen.shared.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(HttpServletRequest http) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(userService.getProfile(userId), requestId(http));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse<PageResponse<UserSummaryResponse>> list(Pageable pageable,
                                                              @RequestParam(required = false) String q,
                                                              HttpServletRequest http) {
        return ApiResponse.ok(userService.listUsers(pageable, q), requestId(http));
    }

    private String requestId(HttpServletRequest request) {
        Object value = request.getAttribute("correlationId");
        return value == null ? null : value.toString();
    }
}
