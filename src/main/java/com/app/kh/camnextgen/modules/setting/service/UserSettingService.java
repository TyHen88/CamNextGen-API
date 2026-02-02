package com.app.kh.camnextgen.modules.setting.service;

import com.app.kh.camnextgen.modules.setting.dto.UpsertSettingRequest;
import com.app.kh.camnextgen.modules.setting.dto.UserSettingResponse;

public interface UserSettingService {

    /**
     * Create or merge settings for the current user (POST).
     * Inserts/merges key-value pairs into settings_json.
     */
    UserSettingResponse create(UpsertSettingRequest request);

    /**
     * Full update: replace settings_json with the given map (PUT).
     */
    UserSettingResponse updatePut(UpsertSettingRequest request);

    /**
     * Partial update: merge given keys into existing settings_json (PATCH).
     */
    UserSettingResponse updatePatch(UpsertSettingRequest request);

    /**
     * Soft delete: set is_deleted = true for the current user's setting row.
     */
    void delete();

    /**
     * Get current user's settings as key-value map (excludes soft-deleted).
     */
    UserSettingResponse get();
}
