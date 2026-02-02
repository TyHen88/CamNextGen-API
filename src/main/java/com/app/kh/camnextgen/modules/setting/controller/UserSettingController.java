package com.app.kh.camnextgen.modules.setting.controller;

import com.app.kh.camnextgen.modules.setting.dto.UpsertSettingRequest;
import com.app.kh.camnextgen.modules.setting.dto.UserSettingResponse;
import com.app.kh.camnextgen.modules.setting.service.UserSettingService;
import com.app.kh.camnextgen.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-settings")
public class UserSettingController {

    private final UserSettingService userSettingService;

    public UserSettingController(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }

    @PostMapping
    public ApiResponse<UserSettingResponse> create(@Valid @RequestBody UpsertSettingRequest request,
            HttpServletRequest http) {
        return ApiResponse.ok(userSettingService.create(request), requestId(http));
    }

    @PutMapping
    public ApiResponse<UserSettingResponse> updatePut(@Valid @RequestBody UpsertSettingRequest request,
            HttpServletRequest http) {
        return ApiResponse.ok(userSettingService.updatePut(request), requestId(http));
    }

    @PatchMapping
    public ApiResponse<UserSettingResponse> updatePatch(@Valid @RequestBody UpsertSettingRequest request,
            HttpServletRequest http) {
        return ApiResponse.ok(userSettingService.updatePatch(request), requestId(http));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        userSettingService.delete();
    }

    @GetMapping
    public ApiResponse<UserSettingResponse> get(HttpServletRequest http) {
        return ApiResponse.ok(userSettingService.get(), requestId(http));
    }

    private static String requestId(HttpServletRequest request) {
        Object value = request.getAttribute("correlationId");
        return value == null ? null : value.toString();
    }
}
