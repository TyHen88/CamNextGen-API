package com.app.kh.camnextgen.modules.setting.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Request body for POST (insert) and PUT/PATCH (update) of settings.
 * Sent as key-value pairs; stored in settings_json.
 */
public record UpsertSettingRequest(
        @NotNull(message = "settings must not be null") Map<String, Object> settings) {
}
