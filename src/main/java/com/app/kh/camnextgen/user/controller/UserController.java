package com.app.kh.camnextgen.user.controller;

import com.app.kh.camnextgen.user.dto.MenuPermissionResponse;
import com.app.kh.camnextgen.user.dto.UserResponse;
import com.app.kh.camnextgen.user.dto.UserSummaryResponse;
import com.app.kh.camnextgen.user.service.UserService;
import com.app.kh.camnextgen.shared.response.ApiResponse;
import com.app.kh.camnextgen.shared.response.PageResponse;
import com.app.kh.camnextgen.shared.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/me/menus")
    public ApiResponse<List<MenuPermissionResponse>> myMenus(HttpServletRequest http) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(userService.listMyMenus(userId), requestId(http));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ_ALL')")
    public ApiResponse<PageResponse<UserSummaryResponse>> list(Pageable pageable,
                                                              @RequestParam(required = false) String q,
                                                              HttpServletRequest http) {
        return ApiResponse.ok(userService.listUsers(pageable, q), requestId(http));
    }

    @PostMapping("/{id}/promote-instructor")
    @PreAuthorize("hasAuthority('ROLE_PROMOTE_INSTRUCTOR')")
    public ApiResponse<UserResponse> promoteInstructor(@PathVariable Long id, HttpServletRequest http) {
        Long actorUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(userService.promoteToInstructor(id, actorUserId), requestId(http));
    }

    private String requestId(HttpServletRequest request) {
        Object value = request.getAttribute("correlationId");
        return value == null ? null : value.toString();
    }
}
