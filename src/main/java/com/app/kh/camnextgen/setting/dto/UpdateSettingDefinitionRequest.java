package com.app.kh.camnextgen.setting.dto;

import jakarta.validation.constraints.Size;

public record UpdateSettingDefinitionRequest(
        @Size(max = 50) String groupCode,
        @Size(max = 120) String name,
        String description,
        Boolean active,
        Integer sortOrder) {
}
