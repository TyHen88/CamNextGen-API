package com.app.kh.camnextgen.setting.dto;

import java.time.Instant;

/**
 * Setting definition for display (name, description, group, etc.).
 */
public record SettingDefinitionResponse(
        Long id,
        String settingKey,
        String groupCode,
        String name,
        String description,
        boolean active,
        int sortOrder,
        Instant createdAt,
        Instant updatedAt) {
}
