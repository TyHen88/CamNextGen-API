package com.app.kh.camnextgen.modules.user.controller;

import com.app.kh.camnextgen.modules.user.dto.UserFilter;
import com.app.kh.camnextgen.modules.user.dto.UserResponse;
import com.app.kh.camnextgen.modules.user.dto.UserSummaryResponse;
import com.app.kh.camnextgen.modules.user.service.UserService;
import com.app.kh.camnextgen.shared.api.ApiResponse;
import com.app.kh.camnextgen.shared.api.PageResponse;
import com.app.kh.camnextgen.shared.security.CurrentUser;
import org.slf4j.MDC;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me() {
        Long userId = CurrentUser.getUserId();
        return ApiResponse.ok(userService.getProfile(userId), requestId());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse<PageResponse<UserSummaryResponse>> list(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "id,desc") String sort
    ) {
        String[] sortParts = sort.split(",");
        String sortField = sortParts.length > 0 ? sortParts[0] : "id";
        String sortDirection = sortParts.length > 1 ? sortParts[1] : "desc";
        Sort sortSpec = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sortSpec);
        UserFilter filter = new UserFilter(email, status);
        return ApiResponse.ok(userService.listUsers(filter, pageable), requestId());
    }

    private String requestId() {
        String value = MDC.get("correlationId");
        return value == null ? "" : value;
    }
}
