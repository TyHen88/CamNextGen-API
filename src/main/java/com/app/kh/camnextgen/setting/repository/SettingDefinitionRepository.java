package com.app.kh.camnextgen.setting.repository;

import com.app.kh.camnextgen.setting.domain.SettingDefinition;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingDefinitionRepository extends JpaRepository<SettingDefinition, Long> {

    Optional<SettingDefinition> findBySettingKey(String settingKey);

    List<SettingDefinition> findByGroupCodeOrderBySortOrderAscSettingKeyAsc(String groupCode);

    List<SettingDefinition> findByActiveTrueOrderByGroupCodeAscSortOrderAscSettingKeyAsc();
}
