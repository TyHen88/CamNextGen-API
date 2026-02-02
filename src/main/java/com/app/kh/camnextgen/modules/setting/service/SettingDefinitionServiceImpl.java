package com.app.kh.camnextgen.modules.setting.service;

import com.app.kh.camnextgen.modules.setting.domain.SettingDefinition;
import com.app.kh.camnextgen.modules.setting.dto.CreateSettingDefinitionRequest;
import com.app.kh.camnextgen.modules.setting.dto.SettingDefinitionResponse;
import com.app.kh.camnextgen.modules.setting.dto.UpdateSettingDefinitionRequest;
import com.app.kh.camnextgen.modules.setting.repository.SettingDefinitionRepository;
import com.app.kh.camnextgen.shared.exception.BusinessException;
import com.app.kh.camnextgen.shared.exception.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingDefinitionServiceImpl implements SettingDefinitionService {

    private final SettingDefinitionRepository settingDefinitionRepository;

    public SettingDefinitionServiceImpl(SettingDefinitionRepository settingDefinitionRepository) {
        this.settingDefinitionRepository = settingDefinitionRepository;
    }

    @Override
    @Transactional
    public SettingDefinitionResponse create(CreateSettingDefinitionRequest request) {
        if (settingDefinitionRepository.findBySettingKey(request.settingKey()).isPresent()) {
            throw new BusinessException("SETTING_KEY_EXISTS", "Setting key already exists: " + request.settingKey());
        }
        SettingDefinition entity = new SettingDefinition();
        entity.setSettingKey(request.settingKey());
        entity.setGroupCode(request.groupCode());
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setActive(request.active() != null ? request.active() : true);
        entity.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        settingDefinitionRepository.save(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public SettingDefinitionResponse update(Long id, UpdateSettingDefinitionRequest request) {
        SettingDefinition entity = settingDefinitionRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("SETTING_DEFINITION_NOT_FOUND", "Setting definition not found"));
        if (request.groupCode() != null)
            entity.setGroupCode(request.groupCode());
        if (request.name() != null)
            entity.setName(request.name());
        if (request.description() != null)
            entity.setDescription(request.description());
        if (request.active() != null)
            entity.setActive(request.active());
        if (request.sortOrder() != null)
            entity.setSortOrder(request.sortOrder());
        settingDefinitionRepository.save(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public SettingDefinitionResponse getById(Long id) {
        SettingDefinition entity = settingDefinitionRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("SETTING_DEFINITION_NOT_FOUND", "Setting definition not found"));
        return toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!settingDefinitionRepository.existsById(id)) {
            throw new NotFoundException("SETTING_DEFINITION_NOT_FOUND", "Setting definition not found");
        }
        settingDefinitionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettingDefinitionResponse> list() {
        Sort sort = Sort.by(
                Sort.Order.asc("groupCode"),
                Sort.Order.asc("sortOrder"),
                Sort.Order.asc("settingKey"));
        return settingDefinitionRepository.findAll(sort).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettingDefinitionResponse> listActive() {
        return settingDefinitionRepository.findByActiveTrueOrderByGroupCodeAscSortOrderAscSettingKeyAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettingDefinitionResponse> listByGroup(String groupCode) {
        return settingDefinitionRepository.findByGroupCodeOrderBySortOrderAscSettingKeyAsc(groupCode)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private SettingDefinitionResponse toResponse(SettingDefinition entity) {
        return new SettingDefinitionResponse(
                entity.getId(),
                entity.getSettingKey(),
                entity.getGroupCode(),
                entity.getName(),
                entity.getDescription(),
                entity.isActive(),
                entity.getSortOrder(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
