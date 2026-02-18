package com.app.kh.camnextgen.modules.setting.controller;

import com.app.kh.camnextgen.modules.setting.dto.CreateSettingDefinitionRequest;
import com.app.kh.camnextgen.modules.setting.dto.SettingDefinitionResponse;
import com.app.kh.camnextgen.modules.setting.dto.UpdateSettingDefinitionRequest;
import com.app.kh.camnextgen.modules.setting.service.SettingDefinitionService;
import com.app.kh.camnextgen.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-settings/definitions")
public class SettingDefinitionController {

    private final SettingDefinitionService settingDefinitionService;

    public SettingDefinitionController(SettingDefinitionService settingDefinitionService) {
        this.settingDefinitionService = settingDefinitionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SettingDefinitionResponse> create(@Valid @RequestBody CreateSettingDefinitionRequest request,
            HttpServletRequest http) {
        return ApiResponse.ok(settingDefinitionService.create(request), requestId(http));
    }

    @PutMapping("/{id}")
    public ApiResponse<SettingDefinitionResponse> update(@PathVariable Long id,
            @Valid @RequestBody UpdateSettingDefinitionRequest request,
            HttpServletRequest http) {
        return ApiResponse.ok(settingDefinitionService.update(id, request), requestId(http));
    }

    @GetMapping("/{id}")
    public ApiResponse<SettingDefinitionResponse> getById(@PathVariable Long id, HttpServletRequest http) {
        return ApiResponse.ok(settingDefinitionService.getById(id), requestId(http));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        settingDefinitionService.delete(id);
    }

    @GetMapping
    public ApiResponse<List<SettingDefinitionResponse>> list(HttpServletRequest http) {
        return ApiResponse.ok(settingDefinitionService.list(), requestId(http));
    }

    @GetMapping("/active")
    public ApiResponse<List<SettingDefinitionResponse>> listActive(HttpServletRequest http) {
        return ApiResponse.ok(settingDefinitionService.listActive(), requestId(http));
    }

    @GetMapping("/by-group")
    public ApiResponse<List<SettingDefinitionResponse>> listByGroup(@RequestParam String groupCode,
            HttpServletRequest http) {
        return ApiResponse.ok(settingDefinitionService.listByGroup(groupCode), requestId(http));
    }

    private static String requestId(HttpServletRequest request) {
        Object value = request.getAttribute("correlationId");
        return value == null ? null : value.toString();
    }
}
