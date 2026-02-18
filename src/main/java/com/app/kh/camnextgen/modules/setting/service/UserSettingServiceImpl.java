package com.app.kh.camnextgen.modules.setting.service;

import com.app.kh.camnextgen.modules.setting.domain.UserSetting;
import com.app.kh.camnextgen.modules.setting.dto.UpsertSettingRequest;
import com.app.kh.camnextgen.modules.setting.dto.UserSettingResponse;
import com.app.kh.camnextgen.modules.setting.repository.UserSettingRepository;
import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.modules.user.repository.UserRepository;
import com.app.kh.camnextgen.shared.exception.NotFoundException;
import com.app.kh.camnextgen.shared.security.SecurityUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingServiceImpl implements UserSettingService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final UserSettingRepository userSettingRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserSettingServiceImpl(UserSettingRepository userSettingRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper) {
        this.userSettingRepository = userSettingRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public UserSettingResponse create(UpsertSettingRequest request) {
        Long userId = requireCurrentUserId();
        UserSetting entity = userSettingRepository.findByUserId(userId)
                .orElseGet(() -> createNew(userId));
        if (entity.isDeleted()) {
            entity.setDeleted(false);
        }
        Map<String, Object> existing = jsonToMap(entity.getSettingsJson());
        existing.putAll(request.settings());
        entity.setSettingsJson(mapToJson(existing));
        userSettingRepository.save(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public UserSettingResponse updatePut(UpsertSettingRequest request) {
        Long userId = requireCurrentUserId();
        UserSetting entity = userSettingRepository.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new NotFoundException("USER_SETTING_NOT_FOUND", "User setting not found"));
        entity.setSettingsJson(mapToJson(request.settings()));
        userSettingRepository.save(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public UserSettingResponse updatePatch(UpsertSettingRequest request) {
        Long userId = requireCurrentUserId();
        UserSetting entity = userSettingRepository.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new NotFoundException("USER_SETTING_NOT_FOUND", "User setting not found"));
        Map<String, Object> existing = jsonToMap(entity.getSettingsJson());
        existing.putAll(request.settings());
        entity.setSettingsJson(mapToJson(existing));
        userSettingRepository.save(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public void delete() {
        Long userId = requireCurrentUserId();
        UserSetting entity = userSettingRepository.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new NotFoundException("USER_SETTING_NOT_FOUND", "User setting not found"));
        entity.setDeleted(true);
        userSettingRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSettingResponse get() {
        Long userId = requireCurrentUserId();
        return userSettingRepository.findByUserIdAndDeletedFalse(userId)
                .map(this::toResponse)
                .orElseGet(() -> new UserSettingResponse(null, userId, Collections.emptyMap()));
    }

    private UserSetting createNew(Long userId) {
        User user = userRepository.getReferenceById(userId);
        UserSetting entity = new UserSetting();
        entity.setUser(user);
        entity.setSettingsJson(mapToJson(Collections.emptyMap()));
        entity.setDeleted(false);
        return entity;
    }

    private UserSettingResponse toResponse(UserSetting entity) {
        Map<String, Object> settings = jsonToMap(entity.getSettingsJson());
        return new UserSettingResponse(entity.getId(), entity.getUser().getId(), settings);
    }

    private Map<String, Object> jsonToMap(JsonNode node) {
        if (node == null || node.isNull()) {
            return Collections.emptyMap();
        }
        return objectMapper.convertValue(node, MAP_TYPE);
    }

    private JsonNode mapToJson(Map<String, Object> map) {
        return objectMapper.valueToTree(map);
    }

    private static Long requireCurrentUserId() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User not authenticated");
        }
        return userId;
    }
}
