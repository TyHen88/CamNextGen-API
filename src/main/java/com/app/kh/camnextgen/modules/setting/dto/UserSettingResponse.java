package com.app.kh.camnextgen.modules.setting.dto;

import java.util.Map;

/**
 * User settings returned as key-value pairs (mapped from settings_json).
 */
public record UserSettingResponse(Long id, Long userId, Map<String, Object> settings) {
}
