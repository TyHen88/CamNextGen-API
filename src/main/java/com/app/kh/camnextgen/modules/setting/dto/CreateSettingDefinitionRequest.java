package com.app.kh.camnextgen.modules.setting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSettingDefinitionRequest(
        @NotBlank @Size(max = 120) String settingKey,
        @NotBlank @Size(max = 50) String groupCode,
        @NotBlank @Size(max = 120) String name,
        String description,
        Boolean active,
        Integer sortOrder) {
}
