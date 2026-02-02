package com.app.kh.camnextgen.modules.setting.service;

import com.app.kh.camnextgen.modules.setting.dto.CreateSettingDefinitionRequest;
import com.app.kh.camnextgen.modules.setting.dto.SettingDefinitionResponse;
import com.app.kh.camnextgen.modules.setting.dto.UpdateSettingDefinitionRequest;
import java.util.List;

public interface SettingDefinitionService {

    SettingDefinitionResponse create(CreateSettingDefinitionRequest request);

    SettingDefinitionResponse update(Long id, UpdateSettingDefinitionRequest request);

    SettingDefinitionResponse getById(Long id);

    void delete(Long id);

    List<SettingDefinitionResponse> list();

    /**
     * List all active definitions ordered by group_code, sort_order (for display).
     */
    List<SettingDefinitionResponse> listActive();

    /**
     * List definitions by group_code (for display).
     */
    List<SettingDefinitionResponse> listByGroup(String groupCode);
}
